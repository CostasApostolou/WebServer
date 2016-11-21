package com.qitasc.exercise.http

import com.qitasc.exercise.server.*
import java.io.*
import java.nio.file.*
import java.time.*

class Resource (val txt : String, val type : String)

class HttpRequestProcessorImpl : Processor {

	private val routes = mutableMapOf<String, Resource>()

	fun registerRoute(url : String, resource : Resource){
		routes.put(url, resource)
	}

	override fun prepareResponse(req: Request): HttpResp {

		req as HttpReq
		val resp = HttpResp()

		when (req.header.method) {
			Methods.HEAD.name -> handleHead(req, resp)
			Methods.GET.name -> handleGet(req, resp)
		}

		return resp
	}

	private fun handleGet(req: HttpReq, resp: HttpResp) {
		handleHead(req, resp)

		val resource = req.header.uri

		if (routes.contains(resource)){
			addBody(routes[resource]?.txt?.toByteArray(), routes[resource]?.type, resp)
		} else {
			addBody(openResource(resource), getResourceType(resource), resp)
		}

//		when (resource) {
//			"/" -> addBody("This is the root folder".toByteArray(), "text/plain", resp)
//			"/hello" -> addBody("<html><body><h1>Hello, World!</h1></body></html>".toByteArray(), "text/html", resp)
//			"internal_error" -> addBody("<html><body><h1>${Results.INTERNAL_ERROR.code} ${Results.INTERNAL_ERROR.reason}</h1><h2>An internal error occurred. Sorry for the inconvenience</h2></body></html>".toByteArray(), "text/html", resp)
//			else -> addBody(openResource(resource), getResourceType(resource), resp)
//		}
	}

	private fun handleHead(req: HttpReq, resp: HttpResp) {

		prepareStatusLine(req, resp)
		addDate(resp)
		addServer(resp)
	}

	private fun prepareStatusLine(req: HttpReq, resp: HttpResp) {
		resp.protocol = req.header.protocol
		resp.result = Results.OK
	}

	private fun addDate(resp: HttpResp) {
		resp.headerFields.add("Date: " + LocalDateTime.now())
	}

	private fun addServer(resp: HttpResp) {
		resp.headerFields.add("Server: Costas")
	}

	private fun addBody(body: ByteArray?, type: String?, resp: HttpResp) {
		resp.headerFields.add("Content-Length: " + body?.size)
		resp.headerFields.add("Content-Type: " + type)
		resp.body = body
	}

	private fun getResourceType(resource: String) =
			when {
				resource.endsWith("png") -> "image/png"
				resource.endsWith("jpeg") -> "image/jpeg"
				resource.endsWith("bmp") -> "image/bmp"
				resource.endsWith("gif") -> "image/gif"
				resource.endsWith("htm") || resource.endsWith("html") -> "text/html"
				else -> "text/plain"
			}

	fun openResource(resource: String): ByteArray {
		try {
			return Files.readAllBytes(Paths.get(System.getProperty("user.dir"), resource))
		} catch (e: IOException) {
			throw RuntimeException(Results.PAGE_NOT_FOUND.reason)
		}
	}
}




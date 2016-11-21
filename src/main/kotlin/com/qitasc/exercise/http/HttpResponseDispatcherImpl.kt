package com.qitasc.exercise.http


import com.qitasc.exercise.server.*
import java.io.*

class HttpResponseDispatcherImpl : Dispatcher {


	override fun sendResponse(outputStream: OutputStream, resp: Response) {
		outputStream.write(resp.toBytes())
		outputStream.flush()
	}

	fun sendBadRequestResponse(outputStream: OutputStream) {
		val req = HttpReq(HttpReqHeader("GET", "/images/400-badreq.jpeg", "HTTP/1.1", mapOf()), byteArrayOf())
		val resp = HttpRequestProcessorImpl().prepareResponse(req)
		resp.result = Results.BAD_REQUEST
		sendResponse(outputStream, resp)
	}

	fun sendPageNotFoundResponse(outputStream: OutputStream) {
		val req = HttpReq(HttpReqHeader("GET", "/images/404-error.jpeg", "HTTP/1.1", mapOf()), byteArrayOf())
		val resp = HttpRequestProcessorImpl().prepareResponse(req)
		resp.result = Results.PAGE_NOT_FOUND
		sendResponse(outputStream, resp)
	}

	fun sendInternalError(outputStream: OutputStream) {
		val req = HttpReq(HttpReqHeader("GET", "internal_error", "HTTP/1.1", mapOf()), byteArrayOf())
		val resp = HttpRequestProcessorImpl().prepareResponse(req)
		resp.result = Results.INTERNAL_ERROR
		sendResponse(outputStream, resp)
	}

}
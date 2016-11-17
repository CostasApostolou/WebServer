import java.io.*
import java.nio.file.*
import java.time.*

class HttpRequestProcessorImpl : HttpRequestProcessor {

	private val resp = HttpResp()

	override fun prepareHttpResponse(req: HttpReq): HttpResp {

		when (req.header.method) {
			Methods.HEAD.name -> handleHead(req)
			Methods.GET.name -> handleGet(req)
		}

		return resp
	}

	private fun handleGet(req: HttpReq) {
		handleHead(req)

		val resource = req.header.uri

		when (resource){
			"/" -> addBodyToResp("This is the root folder".toByteArray(), "text/plain")
			"/hello" -> addBodyToResp("<html><body><h1>Hello, World!</h1></body></html>".toByteArray(), "text/html")
			else -> addBodyToResp(openResource(resource), getResourceType(resource))
		}
	}

	private fun handleHead(req: HttpReq) {

		prepareStatusLine(req)
		addDateToResp()
		addServerToResp()
	}

	private fun prepareStatusLine(req: HttpReq) {
		resp.protocol = req.header.protocol
		resp.result = Results.OK
	}

	private fun addDateToResp() {
		resp.headerFields.add("Date: " + LocalDateTime.now())
	}

	private fun addServerToResp() {
		resp.headerFields.add("Server: Costas")
	}

	private fun addBodyToResp(body: ByteArray, type: String) {
		resp.headerFields.add("Content-Length: " + body.size)
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

	fun openResource(resource: String) : ByteArray {
		try {
			return Files.readAllBytes(Paths.get(System.getProperty("user.dir"), resource))
		} catch (e : IOException){
			throw RuntimeException(Results.PAGE_NOT_FOUND.reason)
		}
	}



}




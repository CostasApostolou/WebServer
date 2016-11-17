import java.io.*

class HttpResponseDispatcherImpl : HttpResponseDispatcher{

	override fun sendHttpResponse(outputStream: OutputStream, resp: HttpResp) {
		outputStream.write(resp.toBytes())
		outputStream.flush()
	}

	override fun sendBadRequestResponse(outputStream: OutputStream) {
		val req = HttpReq(HttpReqHeader("GET", "/images/400-badreq.jpeg", "HTTP/1.1", mapOf()), byteArrayOf())
		val resp = HttpRequestProcessorImpl().prepareHttpResponse(req)
		resp.result = Results.BAD_REQUEST
		sendHttpResponse(outputStream, resp)
	}

	override fun sendPageNotFoundResponse(outputStream: OutputStream) {
		val req = HttpReq(HttpReqHeader("GET", "/images/404-error.jpeg", "HTTP/1.1", mapOf()), byteArrayOf())
		val resp = HttpRequestProcessorImpl().prepareHttpResponse(req)
		resp.result = Results.PAGE_NOT_FOUND
		sendHttpResponse(outputStream, resp)
	}

}
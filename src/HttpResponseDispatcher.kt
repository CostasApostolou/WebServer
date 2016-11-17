import java.io.*

interface HttpResponseDispatcher {

	fun sendHttpResponse(outputStream : OutputStream, resp : HttpResp)
	fun sendBadRequestResponse(outputStream: OutputStream)
	fun sendPageNotFoundResponse(outputStream: OutputStream)
}
import java.io.*
import java.net.*

interface HttpRequestParser {
	fun parse(inputStream: InputStream): HttpReq
}

interface HttpRequestProcessor {
	fun prepareHttpResponse(req: HttpReq): HttpResp
}

interface HttpResponseDispatcher {
	fun sendHttpResponse(outputStream: OutputStream, resp: HttpResp)
	fun sendBadRequestResponse(outputStream: OutputStream)
	fun sendPageNotFoundResponse(outputStream: OutputStream)
	fun sendInternalError(outputStream: OutputStream)
}

class Server(val port: Int,
			 val parser: HttpRequestParser = HttpRequestParserImpl(),
			 val processor: HttpRequestProcessor = HttpRequestProcessorImpl(),
			 val dispatcher: HttpResponseDispatcher = HttpResponseDispatcherImpl()) {

	private val threadPool = mutableListOf<Thread>()

	fun startServer(){
		val serverConnect = ServerSocket(port)
		println("Web server is listening on port " + port)
		while (true) {
			val socket = serverConnect.accept()
			val handler = HttpRequestHandler(socket, parser, processor, dispatcher)
			val thread = Thread(handler)
			thread.start()
			threadPool.add(thread)
		}
	}

	fun stopServer() = threadPool.forEach(Thread::join)

}
import java.net.*

class HttpRequestHandler(val remote : Socket,
						 val parser: HttpRequestParser = HttpRequestParserImpl(),
						 val processor: HttpRequestProcessor = HttpRequestProcessorImpl(),
						 val dispatcher: HttpResponseDispatcher = HttpResponseDispatcherImpl()) : Runnable{

	override fun run() {

		println("Connection established")
		remote.use {

			try {
				val req = parser.parse(it.inputStream)
				val resp = processor.prepareHttpResponse(req)
				dispatcher.sendHttpResponse(it.outputStream, resp)
			} catch (rte: RuntimeException) {
				when (rte.message) {
					Results.PAGE_NOT_FOUND.reason -> dispatcher.sendPageNotFoundResponse(it.outputStream)
					Results.BAD_REQUEST.reason -> dispatcher.sendBadRequestResponse(it.outputStream)
					else -> {
						it.inputStream.skip(it.inputStream.available().toLong())
						dispatcher.sendInternalError(it.outputStream)
					}
				}
			}
		}
	}
}
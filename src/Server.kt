import java.net.*


private const val PORT = 12345

class Server(val remote: Socket) : Runnable {

	override fun run() {

		println("Connection established")
		remote.use {

			try{
				val req = HttpRequestParserImpl().parse(it.inputStream)
				val resp = HttpRequestProcessorImpl().prepareHttpResponse(req)
				HttpResponseDispatcherImpl().sendHttpResponse(it.outputStream, resp)
			} catch (rte : RuntimeException){
				when (rte.message){
					Results.PAGE_NOT_FOUND.reason -> HttpResponseDispatcherImpl().sendPageNotFoundResponse(it.outputStream)
					Results.BAD_REQUEST.reason -> HttpResponseDispatcherImpl().sendBadRequestResponse(it.outputStream)
					else -> println("here" + rte.message)
				}
			}
		}
	}
}


fun main(args: Array<String>) {

    val serverConnect = ServerSocket(PORT)

    println("Web server is listening on port " + PORT)

    while (true) {
        val socket = serverConnect.accept()
        val thread = Thread(Server(socket))
        thread.start()
    }

}
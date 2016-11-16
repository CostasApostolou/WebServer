import java.io.*
import java.net.*
import java.util.*


private const val PORT = 12345

class Server(val remote: Socket) : Runnable {

	override fun run() {

		println("Connection established")
		remote.use {
			val req = receiveHttpRequest(it.inputStream)

			println("HTTP REQUEST : \n" + req)

			val resp = HttpResp().prepareHttpResponse(req)

			println("HTTP RESPONSE : \n" + resp)

			it.outputStream.write(resp.toBytes())
			it.outputStream.flush()
		}
	}

	private fun receiveHttpRequest(inputStream: InputStream): HttpReq {

		val header = readHttpHeader(inputStream)
		val httpReq = HttpReq(header)
		httpReq.validate()

		if (httpReq.hasBody()) {
			//TODO : handle HTTP requests with body
		}

		return httpReq
	}

	private fun readHttpHeader(inStr: InputStream): String {
		var info: Int
		var counter: Int = 4
		val headerBytes: ArrayList<Byte> = ArrayList()

		do {
			info = inStr.read()
			if (info == '\n'.toInt() || info == '\r'.toInt()) {
				counter--
			} else if (info == -1) {
				counter = 0
			} else {
				counter = 4
			}
			headerBytes.add(info.toByte())
		} while (counter != 0)

		return String(headerBytes.toByteArray())
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

//fun main(args: Array<String>) {
//	val server = Server(PORT)
//
//	Runtime.getRuntime().addShutdownHook(object : Thread() {
//		override fun run() {
//			server.stop()
//		}
//	})
//	server.start()
//	while (true) {
//
//	}
//}
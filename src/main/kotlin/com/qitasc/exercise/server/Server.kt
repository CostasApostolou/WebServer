package com.qitasc.exercise.server

import java.io.*
import java.net.*

interface Request
interface Response {
	fun toBytes(): ByteArray
}

interface Parser {
	fun parse(inputStream: InputStream): Request
}

interface Processor {
	fun prepareResponse(req: Request): Response
}

interface Dispatcher {
	fun sendResponse(outputStream: OutputStream, resp: Response)
}

interface Handler : Runnable

interface HandlerBuilder {
	fun setSocket(socket: Socket)
	fun getResult(): Handler
}

class Server(val port: Int,
			 val handlerBuilder: HandlerBuilder) {

	private val threadPool = mutableListOf<Thread>()
	private var serverConnect: ServerSocket? = null

	fun startServer() {
		try {
			val serverConnect = ServerSocket(port)
			println("Web server is listening on port " + port)
			this.serverConnect = serverConnect
			while (true) {
				val socket = serverConnect.accept()
				handlerBuilder.setSocket(socket)
				val handler = handlerBuilder.getResult()
				val thread = Thread(handler)
				thread.start()
				threadPool.add(thread)
			}
		} catch (ioe: IOException) {
			println("Server encountered an IO error")
			println("Shutting down...")
		} catch (se: SocketException) {
			println("Server closed")
		} finally {
			serverConnect?.close()
		}
	}

	fun stopServer() {
		try {
			serverConnect?.close()
		} catch (ignored: IOException) {
		}

		threadPool.forEach(Thread::join)
	}

}
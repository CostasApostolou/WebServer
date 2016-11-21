package com.qitasc.exercise.http

import com.qitasc.exercise.server.*
import java.net.*

class HttpRequestHandler(val remote : Socket?,
						 val parser: HttpRequestParserImpl,
						 val processor: HttpRequestProcessorImpl,
						 val dispatcher: HttpResponseDispatcherImpl) : Handler{

	override fun run() {

		println("Connection established")
		remote?.use {

			try {
				val req = parser.parse(it.inputStream)
				val resp = processor.prepareResponse(req)
				dispatcher.sendResponse(it.outputStream, resp)
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
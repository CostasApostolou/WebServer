package com.qitasc.exercise.core

import com.qitasc.exercise.http.*
import com.qitasc.exercise.server.*


private const val PORT = 12345

fun main(args: Array<String>) {

	val parser = HttpRequestParserImpl()
	val processor = HttpRequestProcessorImpl()
	processor.registerRoute("/", Resource("This is the root folder", "text/plain"))
	processor.registerRoute("/hello", Resource("<html><body><h1>Hello, World!</h1></body></html>", "text/html"))
	processor.registerRoute("internal_error", Resource("<html><body><h1>${Results.INTERNAL_ERROR.code} ${Results.INTERNAL_ERROR.reason}</h1><h2>An internal error occurred. Sorry for the inconvenience</h2></body></html>", "text/html"))
	val dispatcher = HttpResponseDispatcherImpl()
	val handlerBuilder = HandlerBuilderImpl(parser, processor, dispatcher)

	Server(PORT, handlerBuilder).startServer()
}

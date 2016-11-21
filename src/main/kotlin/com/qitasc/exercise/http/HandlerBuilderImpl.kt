package com.qitasc.exercise.http

import com.qitasc.exercise.server.*
import java.net.*

class HandlerBuilderImpl(val parser: HttpRequestParserImpl, val processor: HttpRequestProcessorImpl, val dispatcher: HttpResponseDispatcherImpl) : HandlerBuilder {

	private var socket : Socket? = null
//	private var parser : HttpRequestParserImpl? = null
//	private var processor : HttpRequestProcessorImpl? = null
//	private var dispatcher : HttpResponseDispatcherImpl? = null
//
//	fun setParser(parser: HttpRequestParserImpl){
//		this.parser = parser
//	}
//
//	fun setProcessor(processor : HttpRequestProcessorImpl){
//		this.processor = processor
//	}
//
//	fun setDispatcher (dispatcher: HttpResponseDispatcherImpl){
//		this.dispatcher = dispatcher
//	}

	override fun setSocket(remote : Socket) {
		this.socket = remote
	}

	override fun getResult(): HttpRequestHandler {
		return HttpRequestHandler(socket, parser, processor, dispatcher)
	}

}
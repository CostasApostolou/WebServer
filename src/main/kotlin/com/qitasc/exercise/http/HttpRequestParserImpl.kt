package com.qitasc.exercise.http

import com.qitasc.exercise.server.*
import java.io.*

class HttpRequestParserImpl : Parser{

	override fun parse(inputStream: InputStream): HttpReq {

		val method = readMethod(inputStream)
		val uri = readUri(inputStream)
		val protocol = readProtocol(inputStream)
		val map = readHeaderFields(inputStream)
		val body = if (map["Content-Length"] != null) inputStream.readBytes(map["Content-Length"]!!.toInt()) else byteArrayOf()

		return HttpReq(HttpReqHeader(method, uri, protocol, map), body)
	}

	private fun readMethod(inputStream: InputStream): String {

		val bytes = ByteArrayOutputStream()
		var byte = inputStream.read()

		if (byte !in 'A'.toInt()..'Z'.toInt()) {
			inputStream.skip(inputStream.available().toLong())
			throw RuntimeException(Results.BAD_REQUEST.reason)
		}

		while (byte != ' '.toInt()) {
			bytes.write(byte)
			byte = inputStream.read()
		}

		val method = String(bytes.toByteArray())

		if (!Methods.values().any { it.name == method }) {
			inputStream.skip(inputStream.available().toLong())
			throw RuntimeException(Results.BAD_REQUEST.reason)
		}

		return method
	}

	private fun readUri(inputStream: InputStream): String {

		val bytes = ByteArrayOutputStream()
		var byte = inputStream.read()

		while (byte != ' '.toInt()) {
			bytes.write(byte)
			byte = inputStream.read()
		}

		return String(bytes.toByteArray())
	}

	private fun readProtocol(inputStream: InputStream): String {

		val bytes = ByteArrayOutputStream()
		var byte = inputStream.read()

		while (byte != '\r'.toInt()) {
			bytes.write(byte)
			byte = inputStream.read()
		}

		if (inputStream.read() != '\n'.toInt()) {
			inputStream.skip(inputStream.available().toLong())
			throw RuntimeException(Results.BAD_REQUEST.reason)
		} else {
			val protocol = String(bytes.toByteArray())
			if (protocol.matches(Regex("HTTP/\\d\\.\\d"))) {
				 return protocol
			} else {
				inputStream.skip(inputStream.available().toLong())
				throw RuntimeException(Results.BAD_REQUEST.reason)
			}
		}
	}

	private fun readHeaderFields(inputStream: InputStream): Map<String, String> {

		val bytes = ByteArrayOutputStream()
		var counter: Int = 4

		do {
			val byte = inputStream.read()
			when (byte) {
				'\r'.toInt(), '\n'.toInt() -> counter--
				-1 -> counter = 0
				else -> counter = 4
			}
			bytes.write(byte)
		} while (counter != 0)

		val fieldsString = String(bytes.toByteArray()).split("\r\n")
		return fieldsString.map { it.split(": ") }
				.filter { it.size == 2 }
				.map { it[0] to it[1].trim() }
				.toMap()
	}

}
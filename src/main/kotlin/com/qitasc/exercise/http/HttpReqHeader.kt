package com.qitasc.exercise.http

class HttpReqHeader(val method: String, val uri: String, val protocol: String, val map: Map<String, String>)

enum class Methods {
	GET, HEAD//, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE
}
package com.qitasc.exercise.http

import com.qitasc.exercise.server.*

class HttpReq(val header: HttpReqHeader, val body: ByteArray) : Request
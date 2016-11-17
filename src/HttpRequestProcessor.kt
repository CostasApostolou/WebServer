interface HttpRequestProcessor {

	fun prepareHttpResponse(req : HttpReq) : HttpResp
}
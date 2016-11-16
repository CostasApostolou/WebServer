
class HttpReq(headerString: String, val body: ByteArray? = null) {

    val header: HttpReqHeader = HttpReqHeader(headerString)

    fun hasBody() = header.map["Content-Length"] != null

    fun validate() =
            header.decode() && header.validateMethod() && header.validateProtocol() && header.validateResource()

    override fun toString(): String {
        val sb = StringBuilder()

        sb.append(header.toString())
        if (body != null) {
            sb.append(body.toString())
        }

        return sb.toString()
    }

}
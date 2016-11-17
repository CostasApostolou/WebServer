import java.io.File

class HttpReqHeader(val headerAsString: String) {

    var method: String = "GET"
    var uri: String = "/"
    var protocol: String = "HTTP/1.1"
    var map = mapOf<String, String>()
    var res: Results = Results.OK

    fun decode(): Boolean {
        val lines = this.headerAsString.split("\r\n")

        parseRequestLine(lines[0])

        map = lines.drop(1)
                .map { it.split(": ") }
                .filter { it.size == 2 }
                .map { it[0] to it[1] }
                .toMap()

        return res == Results.OK
    }

    fun parseRequestLine(reqLine: String) {
        val tokens = reqLine.split(" ")
        if (tokens.size != 3) {
            res = Results.BAD_REQUEST
        } else {
            this.method = tokens[0]
            this.uri = tokens[1]
            this.protocol = tokens[2]
        }
    }

    fun validateMethod(): Boolean {

		res = when {
			Methods.values().any { it.name == this.method } -> Results.OK
			else -> Results.NOT_IMPLEMENTED
		}

		return res == Results.OK
    }

    fun validateProtocol(): Boolean {
        if (!this.protocol.matches(Regex("HTTP/\\d\\.\\d"))) {
            protocol = "HTTP/1.1"
            res = Results.HTTP_VER_NOT_SUPPORTED
        }
        return res == Results.OK
    }

    fun validateResource(): Boolean {
        when (this.uri) {
            "/", "/hello" -> res = Results.OK
            else -> if (File(System.getProperty("user.dir") + this.uri).isFile) {
                res = Results.OK
            } else {
                res = Results.PAGE_NOT_FOUND
            }
        }

        return res == Results.OK
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("$method $uri $protocol\r\n")
        map.keys.forEach { key -> sb.append(key).append(": ").append(map[key]).append("\r\n") }
        sb.append("\r\n")
        return sb.toString()
    }
}

enum class Methods {
    GET, HEAD//, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE
}
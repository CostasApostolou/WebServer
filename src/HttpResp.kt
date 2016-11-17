import java.io.*

class HttpResp {

    var result : Results = Results.OK
    var protocol: String = ""
    var headerFields = mutableListOf<String>()
    var body: ByteArray? = null

    fun toBytes(): ByteArray {
        val sb = StringBuilder()

        sb.append("$protocol ${result.code} ${result.reason}\r\n")
        headerFields.forEach { sb.append(it).append("\r\n") }
        sb.append("\r\n")

		val out = ByteArrayOutputStream()

		out.write(sb.toString().toByteArray())

        if (body != null) {
			out.write(body)
        }

		return out.toByteArray()
    }
}

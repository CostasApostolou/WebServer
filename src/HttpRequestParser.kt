import java.io.*

interface HttpRequestParser {

	fun parse(inputStream : InputStream) : HttpReq
}





import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 * Created by costas on 11.11.16.
 */
class HttpResp {

    var statusCode: String = ""
    var reason: String = ""
    var protocol: String = ""
    var headerFields = mutableListOf<String>()
    var body: ByteArray? = null


    fun addServer() {
        headerFields.add("Server: Costas")
    }

    fun addConnection(status: String) {
        headerFields.add("Connection: " + status)
    }

    fun addBody(body: ByteArray, type: String) {
        headerFields.add("Content-Length: " + body.size)
        headerFields.add("Content-Type: " + type)
        this.body = body
    }

    fun addResource(resource: String) {
        when (resource) {
            "/" -> addBody("This is the root folder".toByteArray(), "text/plain")
            "/hello" -> addBody("<html><body><h1>Hello, World!</h1></body></html>".toByteArray(), "text/html")
            else -> addBody(openResource(resource), getResourceType(resource))
        }
    }

    fun getResourceType(resource: String) =
            when {
                resource.endsWith("png") -> "image/png"
                resource.endsWith("jpeg") -> "image/jpeg"
                resource.endsWith("bmp") -> "image/bmp"
                resource.endsWith("gif") -> "image/gif"
                resource.endsWith("htm") || resource.endsWith("html") -> "text/html"
                else -> "text/plain"
            }

    fun prepareHttpResponse(req: HttpReq): HttpResp {
        when (req.header.method) {
            Methods.HEAD.name -> handleHead(req)
            else -> handleGet(req)
        }

        return this
    }

    fun handleGet(req: HttpReq) {
        handleHead(req)

        when (statusCode) {
            Results.OK.code -> addResource(req.header.uri)
            Results.PAGE_NOT_FOUND.code -> addResource("/images/404-error.jpeg")
            Results.BAD_REQUEST.code -> addResource("/images/400-badreq.jpeg")
            Results.NOT_IMPLEMENTED.code -> addBody((Results.NOT_IMPLEMENTED.code + " " + Results.NOT_IMPLEMENTED.reason).toByteArray(), "text/plain")
            Results.HTTP_VER_NOT_SUPPORTED.code -> addBody("Requested HTTP version not supported".toByteArray(), "text/plain")
        }
    }

    fun handleHead(req: HttpReq) {

        prepareStatusLine(req)
        addDate(headerFields)
        addServer()
    }

    private fun prepareStatusLine(req: HttpReq) {
        protocol = req.header.protocol
        reason = req.header.res.reason
        statusCode = req.header.res.code
    }

    fun toBytes(): ByteArray {
        val sb = StringBuilder()

        sb.append("$protocol $statusCode $reason\r\n")
        headerFields.forEach { sb.append(it).append("\r\n") }
        sb.append("\r\n")

        val headerToBytes = sb.toString().toByteArray()

        if (body != null) {
            return (headerToBytes.toList() + body!!.toList()).toByteArray()
        } else {
            return headerToBytes
        }
    }

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()

        sb.append("$protocol $statusCode $reason\r\n")
        headerFields.forEach { field -> sb.append(field).append("\r\n") }
        sb.append("\r\n")
        if (body != null) {
            sb.append(String(body ?: ByteArray(0)))
        }

        return sb.toString()
    }
}


private fun addDate(list: MutableList<String>) {
    list.add("Date: " + LocalDateTime.now())
}

fun openResource(resource: String) : ByteArray = Files.readAllBytes(Paths.get(System.getProperty("user.dir"), resource))


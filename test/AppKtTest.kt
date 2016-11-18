import org.junit.*
import org.junit.Assert.*
import java.io.*
import java.net.*
import kotlin.concurrent.*

private const val PORT = 12345

class AppKtTest {



	companion object {
		private val server = Server(PORT)
		@BeforeClass @JvmStatic fun start() {
			thread { server.startServer() }
		}

		@AfterClass @JvmStatic fun stop() {
			server.stopServer()
		}
	}

	@Test
	fun testConnectivity() {
		val sock: Socket = Socket("localhost", PORT)
		sock.close()
	}

	@Test
	fun testRoot_GET() {
		val expectedString = "This is the root folder"

		val respTokens = mutableListOf<String>()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest())
			wr.flush()
			respTokens.addAll(String(getHttpResp(it)).split("\r\n"))
		}

		assertEquals(7, respTokens.size)
		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedString.length.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: text/plain", respTokens[4])
		assertEquals("", respTokens[5])
		assertEquals(expectedString, respTokens[6])
	}

	@Test
	fun testHello_GET() {
		val expectedString = "<html><body><h1>Hello, World!</h1></body></html>"

		val respTokens = mutableListOf<String>()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(prot = "HTTP/1.0", resource = "/hello"))
			wr.flush()
			respTokens.addAll(String(getHttpResp(it)).split("\r\n"))
		}

		assertEquals(7, respTokens.size)
		assertEquals("HTTP/1.0 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedString.length.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: text/html", respTokens[4])
		assertEquals("", respTokens[5])
		assertEquals(expectedString, respTokens[6])
	}

	@Test
	fun testJPEG() {
		val expectedImage = HttpRequestProcessorImpl().openResource("/images/booth.jpeg")

		val respTokens = mutableListOf<String>()
		var respBytes = byteArrayOf()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(resource = "/images/booth.jpeg"))
			wr.flush()
			respBytes = getHttpResp(it)
			respTokens.addAll(String(respBytes).split("\r\n"))
		}

		assertEquals(7, respTokens.size)
		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedImage.size.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: image/jpeg", respTokens[4])
		assertEquals("", respTokens[5])

		val receivedImage = respBytes.copyOfRange(respBytes.size - expectedImage.size, respBytes.size)
		assertArrayEquals(expectedImage, receivedImage)

	}

	@Test
	fun testBMP() {
		val expectedImage = HttpRequestProcessorImpl().openResource("/images/lena.bmp")

		val respTokens = mutableListOf<String>()
		var respBytes = byteArrayOf()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(resource = "/images/lena.bmp"))
			wr.flush()
			respBytes = getHttpResp(it)
			respTokens.addAll(String(respBytes).split("\r\n"))
		}

		assertEquals(7, respTokens.size)
		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedImage.size.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: image/bmp", respTokens[4])
		assertEquals("", respTokens[5])

		val receivedImage = respBytes.copyOfRange(respBytes.size - expectedImage.size, respBytes.size)
		assertArrayEquals(expectedImage, receivedImage)

	}

	@Test
	fun testPNG() {
		val expectedImage = HttpRequestProcessorImpl().openResource("/images/logo.png")

		val respTokens = mutableListOf<String>()
		var respBytes = byteArrayOf()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(resource = "/images/logo.png"))
			wr.flush()
			respBytes = getHttpResp(it)
			respTokens.addAll(String(respBytes).split("\r\n"))
		}

		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedImage.size.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: image/png", respTokens[4])
		assertEquals("", respTokens[5])

		val receivedImage = respBytes.copyOfRange(respBytes.size - expectedImage.size, respBytes.size)
		assertArrayEquals(expectedImage, receivedImage)
	}

	@Test
	fun testBadRequest() {
		val expectedImage = HttpRequestProcessorImpl().openResource("/images/400-badreq.jpeg")

		val respTokens = mutableListOf<String>()
		var respBytes = byteArrayOf()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print("This is an invalid request\r\n\r\n")
			wr.flush()
			respBytes = getHttpResp(it)
			respTokens.addAll(String(respBytes).split("\r\n"))
		}

		assertEquals(7, respTokens.size)
		assertEquals("HTTP/1.1 400 Bad Request", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedImage.size.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: image/jpeg", respTokens[4])
		assertEquals("", respTokens[5])

		val receivedImage = respBytes.copyOfRange(respBytes.size - expectedImage.size, respBytes.size)
		assertArrayEquals(expectedImage, receivedImage)
	}

	@Test
	fun testPageNotFound() {
		val expectedImage = HttpRequestProcessorImpl().openResource("/images/404-error.jpeg")

		val respTokens = mutableListOf<String>()
		var respBytes = byteArrayOf()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(resource = "/invalid"))
			wr.flush()
			respBytes = getHttpResp(it)
			respTokens.addAll(String(respBytes).split("\r\n"))
		}

		assertEquals("HTTP/1.1 404 Not Found", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertTrue(respTokens[3].startsWith("Content-Length:"))
		assertEquals(expectedImage.size.toString(), respTokens[3].split(": ")[1])
		assertEquals("Content-Type: image/jpeg", respTokens[4])
		assertEquals("", respTokens[5])

		val receivedImage = respBytes.copyOfRange(respBytes.size - expectedImage.size, respBytes.size)
		assertArrayEquals(expectedImage, receivedImage)
	}

	@Test
	fun testRoot_HEAD() {

		val respTokens = mutableListOf<String>()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest(meth = "HEAD"))
			wr.flush()
			respTokens.addAll(String(getHttpResp(it)).split("\r\n"))
		}

		assertEquals(5, respTokens.size)
		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertEquals("", respTokens[3])
		assertEquals("", respTokens[4])
	}

	@Test
	fun testHello_HEAD() {

		val respTokens = mutableListOf<String>()
		Socket("localhost", PORT).use {
			val wr = PrintWriter(it.outputStream)
			wr.print(prepareHttpRequest("HEAD", "/hello", "HTTP/1.0"))
			wr.flush()
			respTokens.addAll(String(getHttpResp(it)).split("\r\n"))
		}

		assertEquals(5, respTokens.size)
		assertEquals("HTTP/1.0 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertEquals("", respTokens[3])
		assertEquals("", respTokens[4])
	}

	@Test
	fun testMultipleSerialRequests() {
		var sock: Socket = Socket("localhost", PORT)
		var wr: PrintWriter = PrintWriter(sock.outputStream)

		wr.print(prepareHttpRequest(meth = "HEAD"))
		wr.flush()

		var respBytes = getHttpResp(sock)
		var respTokens = String(respBytes).split("\r\n")

		assertEquals(5, respTokens.size)
		assertEquals("HTTP/1.1 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertEquals("", respTokens[3])
		assertEquals("", respTokens[4])

		sock.close()

		sock = Socket("localhost", PORT)

		wr = PrintWriter(sock.outputStream)

		wr.print(prepareHttpRequest("HEAD", "/hello", "HTTP/1.0"))
		wr.flush()

		respBytes = getHttpResp(sock)
		respTokens = String(respBytes).split("\r\n")

		assertEquals(5, respTokens.size)
		assertEquals("HTTP/1.0 200 OK", respTokens[0])
		assertTrue(respTokens[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens[2])
		assertEquals("", respTokens[3])
		assertEquals("", respTokens[4])

		sock.close()
	}

	@Test
	fun testMultipleParallelRequests() {
		// first connection
		val sock1 = Socket("localhost", PORT)
		val wr1 = PrintWriter(sock1.outputStream)

		// first send request
		wr1.print(prepareHttpRequest(meth = "HEAD"))
		wr1.flush()

		// second connection
		val sock2 = Socket("localhost", PORT)
		val wr2 = PrintWriter(sock2.outputStream)

		// first response
		val respBytes1 = getHttpResp(sock1)
		val respTokens1 = String(respBytes1).split("\r\n")

		assertEquals(5, respTokens1.size)
		assertEquals("HTTP/1.1 200 OK", respTokens1[0])
		assertTrue(respTokens1[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens1[2])
		assertEquals("", respTokens1[3])
		assertEquals("", respTokens1[4])

		//first connection closed
		sock1.close()

		// second send request
		wr2.print(prepareHttpRequest(meth = "HEAD"))
		wr2.flush()

		// second response
		val respBytes2 = getHttpResp(sock2)
		val respTokens2 = String(respBytes2).split("\r\n")

		assertEquals(5, respTokens2.size)
		assertEquals("HTTP/1.1 200 OK", respTokens2[0])
		assertTrue(respTokens1[1].startsWith("Date:"))
		assertEquals("Server: Costas", respTokens2[2])
		assertEquals("", respTokens2[3])
		assertEquals("", respTokens2[4])

		// second connection closed
		sock2.close()
	}

	private fun prepareHttpRequest(meth: String = "GET", resource: String = "/", prot: String = "HTTP/1.1"): String {
		return "$meth $resource $prot\r\nkey: value\r\n\r\n"
	}


	private fun getHttpResp(sock: Socket): ByteArray = sock.inputStream.readBytes()
}


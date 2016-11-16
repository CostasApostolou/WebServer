
enum class Results(val code: String, val reason: String) {
    OK("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    PAGE_NOT_FOUND("404", "Not Found"),
    NOT_IMPLEMENTED("501", "Not Implemented"),
    HTTP_VER_NOT_SUPPORTED("505", "HTTP Version Not Supported")
}
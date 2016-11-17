
enum class Results(val code: String, val reason: String) {
    OK("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    PAGE_NOT_FOUND("404", "Not Found")
}
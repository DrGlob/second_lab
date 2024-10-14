import javax.security.sasl.AuthorizeCallback
import Response as Response



// ResponseMatchersException и 2 наследника: StatusResponseMatchersException и BodyResponseMatchersException,

open class ResponseMatchersException(message: String) : Exception(message)
class StatusResponseMatchersException(message : String) : ResponseMatchersException(message)
class BodyResponseMatchersException(message: String) : ResponseMatchersException(message)
data class Response(
    val code: Int,
    val body: String?,
)
class Client {
    fun perform(code: Int, body: String?) = ResponseActions(code, body)
}

class ResponseActions(code: Int, body: String?) {
    val response: Response = Response(code, body)
    fun andDo(do_: (Response) -> Unit) : ResponseActions {
        do_(response)
        return this
    } // ???
    fun andExpect(expect: ResponseMatchers.() -> Unit) : ResponseActions {
        ResponseMatchers(response).expect()
        return this
    }
//    Response()
}


class ResponseMatchers(var response: Response) {
    fun status(callback: StatusResponseMatchers.() -> Unit) {
        StatusResponseMatchers(response.code).callback()
    }
    fun body(callback: BodyResponseMatchers.() -> Unit) {
        BodyResponseMatchers(response.body).callback();
    }
}

class StatusResponseMatchers(val code : Int) {
    fun isOk() {
        if (code != 200) {
            throw StatusResponseMatchersException("Не тот статус")
        }
    } // если статус не 200, то выбросить исключение
    fun isBadRequest() {
        if (code != 400) {
            throw StatusResponseMatchersException("Не тот статус")
        }
    } // если статус не 400, то выбросить исключение
    fun isInternalServerError() {
        if (code != 500) {
            throw StatusResponseMatchersException("Не тот статус")
        }
    } // если статус не 500, то выбросить исключение
}
class BodyResponseMatchers(val body: String?) {
    fun isNull() {
        if (body != null) {
            throw BodyResponseMatchersException("Тело не пустое")
        }
    } // если тело не пустое, то выбросить исключение
    fun isNotNull() {
        if (body == null) {
            throw BodyResponseMatchersException("Тело пустое")
        }
    } // если тело пустое, то выбросить исключение
}
fun main() {
    val mockClient = Client()
    val response = mockClient.perform(200, "OK")
        .andExpect {
            status {
                isOk()
            }
            body {
                isNotNull()
            }
        }.andDo { response ->
            println(response)
        }.response
}

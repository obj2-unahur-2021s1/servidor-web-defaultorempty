package ar.edu.unahur.obj2.servidorWeb

import com.sun.org.apache.xpath.internal.operations.Bool
import java.net.URI
import java.net.URL
import java.time.LocalDateTime

// Para no tener los códigos "tirados por ahí", usamos un enum que le da el nombre que corresponde a cada código
// La idea de las clases enumeradas es usar directamente sus objetos: CodigoHTTP.OK, CodigoHTTP.NOT_IMPLEMENTED, etc
enum class CodigoHttp(val codigo: Int) {
    OK(200),
    NOT_IMPLEMENTED(501),
    NOT_FOUND(404),
}

class Pedido(val ip: String, val url: String, val fechaHora: LocalDateTime)
class Respuesta(val codigo: CodigoHttp, val body: String, val tiempo: Int, val pedido: Pedido)


class WebServer() {

    fun procesarPeticionSinModulos(pedido: Pedido): Respuesta {
        return if (evaluarProtocolo(pedido))
            return Respuesta(CodigoHttp.OK, "OK", 22, pedido)
        else
            return Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, pedido)
    }

    fun evaluarProtocolo(pedido: Pedido): Boolean {
        return URL(pedido.url).protocol == "http"
    }

}


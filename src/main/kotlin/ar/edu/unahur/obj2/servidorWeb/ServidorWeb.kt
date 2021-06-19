package ar.edu.unahur.obj2.servidorWeb

// import com.sun.org.apache.xpath.internal.operations.Bool
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

    var modulos = mutableListOf<Modulo>()
    var analizadores = mutableListOf<Analizador>()

    fun procesarPeticion(pedido: Pedido): Respuesta {

        if (evaluarProtocolo(pedido))
            return procesarPeticionConModulo(pedido)
        else
            return Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, pedido)
    }

    fun procesarPeticionSinModulos(pedido: Pedido): Respuesta {
        return if (evaluarProtocolo(pedido))
            Respuesta(CodigoHttp.OK, "OK", 22, pedido)
        else
            Respuesta(CodigoHttp.NOT_IMPLEMENTED, "", 10, pedido)
    }

    fun obtenerModuloDeProcesamiento(extension: String): Modulo {
        return modulos.first { it.extensiones.contains(extension) }
    }

    fun existeModulo(extension: String): Boolean {
        return modulos.any { it.extensiones.contains(extension) }
    }

    fun procesarPeticionConModulo(pedido: Pedido): Respuesta {
        return if (existeModulo(evaluarExtension(pedido))) {
            var respuesta = obtenerModuloDeProcesamiento(evaluarExtension(pedido)).procesarPedido(pedido)
            ejecutarAnalizador(respuesta, obtenerModuloDeProcesamiento(evaluarExtension(pedido)))
            respuesta
        } else
            Respuesta(CodigoHttp.NOT_FOUND, "", 10, pedido)
    }

    fun evaluarProtocolo(pedido: Pedido): Boolean {
        return URL(pedido.url).protocol == "http"
    }

    fun evaluarExtension(pedido: Pedido): String {
        return URL(pedido.url).path.substringAfter('.')
    }

    fun evaluarRuta(pedido: Pedido): String {
        return URL(pedido.url).path
    }

    fun ejecutarAnalizador(respuesta: Respuesta, modulo: Modulo) {
        analizadores.forEach {
            it.procesar(respuesta, modulo)
        }
    }
}


class Modulo(var extensiones: MutableList<String>, var retorno: String, var tiempo: Int) {

    fun procesarPedido(pedido: Pedido): Respuesta {
        return Respuesta(CodigoHttp.OK, retorno, tiempo, pedido)
    }

    fun puedeResponder(extension: String) = extensiones.contains(extension)
}



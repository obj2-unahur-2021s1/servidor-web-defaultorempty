package ar.edu.unahur.obj2.servidorWeb

import java.time.LocalDateTime
import java.util.*

interface Analizador {
    fun procesar(respuesta: Respuesta, modulo: Modulo)
}

class AnalizadorDeteccionDeDemora(var demoraMinima: Int) : Analizador {

    var modulosConDemora = mutableListOf<Modulo>()

    fun cantidadDeRespuestasDemoradasPorModulo(modulo: Modulo): Int {
        return modulosConDemora.filter { it.equals(modulo) }.count()
    }

    override fun procesar(respuesta: Respuesta, modulo: Modulo) {
        if (respuesta.tiempo > demoraMinima)
            modulosConDemora.add(modulo)
    }
}

class AnalizadorIpsSospechosa() : Analizador {

    var ipsSospechosas = mutableListOf<String>()
    var mapeado = Hashtable<Respuesta, Modulo>()

    override fun procesar(respuesta: Respuesta, modulo: Modulo) {
        if (ipsSospechosas.contains(respuesta.pedido.ip))
            mapeado.put(respuesta, modulo)
    }

    fun cantidadDePedidosPorIp(ip: String): Int {
        return mapeado.filter { it.key.pedido.ip == ip }.count()
    }

    fun moduloMasConsultado(): Modulo? {
        return mapeado.maxBy { it.key.pedido.url }?.value
    }

    fun conjuntoDeIpsPorRuta(ruta: String): MutableList<String> {
        return mapeado.filter { it.key.pedido.url.contains(ruta) }.keys.map { it.pedido.ip }.toMutableList()
    }
}


class AnalizadorEstadisticas() : Analizador {
    var listaDeRespuestas = mutableListOf<Respuesta>()

    override fun procesar(respuesta: Respuesta, modulo: Modulo) {
        listaDeRespuestas.add(respuesta)
    }

    fun tiempoDeRespuestaPromedio(): Double {
        return listaDeRespuestas.map { it.tiempo }.average()
    }

    fun cantidadDePedidosEntreDosMomentos(inicio: LocalDateTime, fin: LocalDateTime): Int {
        return listaDeRespuestas.filter { it.pedido.fechaHora in inicio..fin }.count()
    }

    fun cantidadDeRespuestasPorStringEnBody(texto: String): Int {
        return listaDeRespuestas.filter { it.body.contains(texto) }.count()
    }

    fun porcentajeDePedidosConRespuestaExitoso(): Int {
        return listaDeRespuestas.filter { it.codigo == CodigoHttp.OK }.count() / listaDeRespuestas.count() * 100
    }
}
package ar.edu.unahur.obj2.servidorWeb

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
    var listaDePedidosContenidosEnElRequest = mutableListOf<Respuesta>()
    var mapeado = Hashtable<Respuesta, Modulo>()

    override fun procesar(respuesta: Respuesta, modulo: Modulo) {
        if (ipsSospechosas.contains(respuesta.pedido.ip))
            mapeado.put(respuesta, modulo)
        // listaDePedidosContenidosEnElRequest.add(respuesta)
    }

    fun cantidadDePedidosPorIp(ip: String): Int {
        //  return listaDePedidosContenidosEnElRequest.filter { it.pedido.ip == ip }.count()
        return mapeado.filter { it.key.pedido.ip == ip }.count()
    }

    fun moduloMasConsultado(): Modulo? {
        // listaDePedidosContenidosEnElRequest.maxByOrNull { it.pedido.url }
        return mapeado.maxBy { it.key.pedido.url }?.value
    }

    //fun conjuntoDeIpsPorRuta(ruta : String) = listaDePedidosContenidosEnElRequest.filter { it.pedido.url.contains(ruta) }
    fun conjuntoDeIpsPorRuta(ruta: String): MutableList<String> {
        return mapeado.filter { it.key.pedido.url.contains(ruta) }.keys.map { it.pedido.ip }.toMutableList()
    }

    fun agregarIPSospechosa(ip: String) = ipsSospechosas.add(ip)
    fun eliminarIPSospechosas(ip: String) = ipsSospechosas.remove(ip)
}
package ar.edu.unahur.obj2.servidorWeb

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
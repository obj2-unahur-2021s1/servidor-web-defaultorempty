package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.LocalTime
import java.net.URL
import java.time.Month

class ServidorWebTest : DescribeSpec({

    var servidor = WebServer()
    var moduloWeb = Modulo(mutableListOf("html", "cshtml"), "modulo de sitios", 15)
    var moduloVideo = Modulo(mutableListOf("mov", "mp4", "flv"), "modulo de videos", 12)
    servidor.modulos.add(moduloWeb)
    servidor.modulos.add(moduloVideo)

    describe("Un servidor web") {
        var servidor = WebServer()

        it("Codigo 200 - Ok ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())

            servidor.procesarPeticionSinModulos(pedido).codigo.shouldBe(CodigoHttp.OK)
        }

        it("Codigo 501 - Not Implemente ") {
            var pedido = Pedido("192.168.1.1", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())

            servidor.procesarPeticionSinModulos(pedido).codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
        }
    }

    describe("probar URL") {
        it("Probar HTTP") {
            URL("http://pepito.com.ar/documentos/doc1.html").protocol.shouldBe("http")
        }

        it("Probar file") {
            URL("http://pepito.com.ar/documentos/doc1.html").file.shouldBe("/documentos/doc1.html")
        }

        it("Probar file subtring after") {
            URL("http://pepito.com.ar/documentos/doc1.html").file.substringAfter('.').shouldBe("html")
        }

        it("Probar host") {
            URL("http://pepito.com.ar/documentos/doc1.html").host.shouldBe("pepito.com.ar")
        }
    }

    describe("Servidor Web") {

        var moduloImagenes = Modulo(mutableListOf("jpg", "png", "gif"), "Retorno modulo Imagenes", 22)
        var moduloAudio = Modulo(mutableListOf("mp3", "mp4", "wav"), "Retorno modulo audio", 22)

        servidor.modulos.add(moduloImagenes)

        describe("probar peticion") {
            it("http invalida") {
                var pedido = Pedido("120.234.56.678", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
            }

            it("formato no valido") {
                var pedido = Pedido("120.234.56.678", "http://pepito.com.ar/documentos/doc1.xhtml", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_FOUND)
            }

            it("formato valido") {
                var pedido = Pedido("120.234.56.678", "http://pepito.com.ar/documentos/doc1.png", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.OK)
            }
        }

        describe("probar modulo audio") {
            servidor.modulos.add(moduloAudio)
            it("http invalida") {
                var pedido = Pedido("123.456.789.1", "https://pepito.com.ar/sonidos/sirena.mp4", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
            }

            it("formato no valido") {
                var pedido = Pedido("123.456.789.1", "http://pepito.com.ar/sonidos/sirena.ogg", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_FOUND)
            }

            it("formato valido") {
                var pedido = Pedido("123.456.789.1", "http://pepito.com.ar/sonidos/sirena.mp4", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.OK)
            }
        }
    }

    describe("Analizador de demora") {

        it("Analizador de demora cantidadDeRespuestasDemoradasPorModulo") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var analizadorDeDemora = AnalizadorDeteccionDeDemora(demoraMinima = 1)
            servidor.analizadores.add(analizadorDeDemora)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            analizadorDeDemora.cantidadDeRespuestasDemoradasPorModulo(moduloWeb).shouldBe(2)
        }

        it("Analizador de demora cantidadDeRespuestasDemoradasPorModuloVideo") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/videos/vuelvos.flv", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/videos/vuelos.flv", LocalDateTime.now())
            var pedido3 = Pedido("192.168.1.1", "http://pepito.com.ar/videos/vuelos.flv", LocalDateTime.now())
            var analizadorDeDemora = AnalizadorDeteccionDeDemora(demoraMinima = 13)
            servidor.analizadores.add(analizadorDeDemora)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            servidor.procesarPeticion(pedido3)

            analizadorDeDemora.cantidadDeRespuestasDemoradasPorModulo(moduloVideo).shouldBe(0)
        }
    }

    describe("Analizador de IP Sospechosa") {
        it("Analizador IP sospechosa cantidadDePedidosPorIp ok ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var ipSospechosa = AnalizadorIpsSospechosa()
            ipSospechosa.ipsSospechosas.add("192.168.1.1")
            servidor.analizadores.add(ipSospechosa)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            ipSospechosa.cantidadDePedidosPorIp("192.168.1.1").shouldBe(2)
        }

        it("Analizador IP sospechosa cantidadDePedidosPorIp  por no ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var ipSospechosa = AnalizadorIpsSospechosa()
            ipSospechosa.ipsSospechosas.add("192.168.9.9")
            servidor.analizadores.add(ipSospechosa)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            ipSospechosa.cantidadDePedidosPorIp("192.168.1.1").shouldBe(0)
        }

        it("Analizador IP sospechosa cantidadDePedidosPorIp ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var ipSospechosa = AnalizadorIpsSospechosa()
            ipSospechosa.ipsSospechosas.add("192.168.9.9")
            servidor.analizadores.add(ipSospechosa)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            ipSospechosa.cantidadDePedidosPorIp("192.168.1.1").shouldBe(0)
        }

        it("Analizador IP sospechosa conjuntoDeIpsPorRuta ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var ipSospechosa = AnalizadorIpsSospechosa()
            ipSospechosa.ipsSospechosas.add("192.168.1.1")
            servidor.analizadores.add(ipSospechosa)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            ipSospechosa.conjuntoDeIpsPorRuta("/documentos/doc1.html").count().shouldBe(2)
        }

        it("Analizador IP sospechosa Modulo mas consultado ") {
            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var ipSospechosa = AnalizadorIpsSospechosa()
            ipSospechosa.ipsSospechosas.add("192.168.1.1")
            servidor.analizadores.add(ipSospechosa)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            ipSospechosa.moduloMasConsultado().shouldBe(moduloWeb)
        }
    }
    describe("Analizador de estadisticas") {

        it("analizador de estadisticas cantidadDeRespuestasPorStrinEnBody") {

            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var estadisticas = AnalizadorEstadisticas()

            servidor.analizadores.add(estadisticas)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            estadisticas.cantidadDeRespuestasPorStringEnBody("sitios").shouldBe(2)
        }

        it("analizador de estadisticas porcentajeDePedidosConRespuestaExitoso") {

            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var estadisticas = AnalizadorEstadisticas()

            servidor.analizadores.add(estadisticas)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            estadisticas.porcentajeDePedidosConRespuestaExitoso().shouldBe(100)
        }

        it("analizador de estadisticas tiempoDeRespuestaPromedio") {

            var pedido = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var pedido2 = Pedido("192.168.1.1", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
            var estadisticas = AnalizadorEstadisticas()

            servidor.analizadores.add(estadisticas)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            estadisticas.tiempoDeRespuestaPromedio().shouldBe(15)
        }

        it("analizador de estadisticas cantidadDePedidosEntreDosMomentos") {
            var pedido = Pedido(
                "192.168.1.1",
                "http://pepito.com.ar/documentos/doc1.html",
                LocalDateTime.of(2021, Month.JUNE, 15, 3, 15)
            )
            var pedido2 = Pedido(
                "192.168.1.1",
                "http://pepito.com.ar/documentos/doc1.html",
                LocalDateTime.of(2021, Month.JUNE, 20, 3, 15)
            )
            var estadisticas = AnalizadorEstadisticas()

            servidor.analizadores.add(estadisticas)
            servidor.procesarPeticion(pedido)
            servidor.procesarPeticion(pedido2)
            estadisticas.cantidadDePedidosEntreDosMomentos(
                LocalDateTime.of(2021, Month.JUNE, 14, 3, 15),
                LocalDateTime.of(2021, Month.JUNE, 21, 3, 15)
            ).shouldBe(2)
        }
    }
})

package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.LocalTime
import java.net.URL

class ServidorWebTest : DescribeSpec({
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
        var servidor = WebServer()
        var moduloImagenes = Modulo(mutableListOf("jpg", "png", "gif"), "Retorno modulo Imagenes", 22)

        servidor.modulos.add(moduloImagenes)

        describe("probar peticion") {
            it("http invalida") {
                var pedido = Pedido("120.234.56.678", "https://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_IMPLEMENTED)
            }

            it("formato no valido") {
                var pedido = Pedido("120.234.56.678", "http://pepito.com.ar/documentos/doc1.html", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.NOT_FOUND)
            }

            it("formato valido") {
                var pedido = Pedido("120.234.56.678", "http://pepito.com.ar/documentos/doc1.png", LocalDateTime.now())
                servidor.procesarPeticion(pedido).codigo.shouldBe(CodigoHttp.OK)
            }
        }
    }
})

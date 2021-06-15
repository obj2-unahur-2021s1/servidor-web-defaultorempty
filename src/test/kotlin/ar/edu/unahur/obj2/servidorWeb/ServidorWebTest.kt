package ar.edu.unahur.obj2.servidorWeb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.LocalTime

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
})

package com.template


import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpClientResponse
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.internal.InternalMockNetwork
import net.corda.testing.node.internal.TestStartedNode
import net.corda.testing.node.internal.cordappsForPackages
import net.corda.testing.node.internal.startFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(VertxUnitRunner::class)
class BraidTest {

    lateinit var mockNetwork: InternalMockNetwork
    lateinit var node1: TestStartedNode
    lateinit var party1: Party
    private var vertx = Vertx.vertx(VertxOptions().setBlockedThreadCheckInterval(1000_000))
    private var client = vertx.createHttpClient(HttpClientOptions()
            .setDefaultPort(8080)
            .setDefaultHost("localhost")
            .setSsl(true)
            .setTrustAll(true)
            .setVerifyHost(false)
    )!!

    @Before
    fun setup(testContext: TestContext) {
        mockNetwork = InternalMockNetwork(cordappsForAllNodes = cordappsForPackages("com.template", "com.template.services"),
                threadPerNode = true, initialNetworkParameters = testNetworkParameters(minimumPlatformVersion = 4))
        node1 = mockNetwork.createPartyNode(CordaX500Name(organisation = "BankA", locality = "New York", country = "US"))
        party1 = node1.services.myInfo.legalIdentities.first()
    }

    @After
    fun after(testContext: TestContext) {
        client.close()
        mockNetwork.stopNodes()
    }

    @Test
    fun `test echo service`(context: TestContext) {
        val async = context.async()
        client.getNow(8080, "localhost", "/api/EchoService/braid", object : Handler<HttpClientResponse> {
            override fun handle(event: HttpClientResponse?) {
                event!!.bodyHandler(object : Handler<Buffer> {
                    override fun handle(event: Buffer?) {
                        println("Response (" + event?.length() + "): ")
                        println(event?.getString(0, event.length()))
                    }

                })
            }
        })

    }

//    @Test
//    fun `test echo service`(testContext: TestContext) {
//        jsonRPC("wss://localhost:8080/api/jsonrpc/daoservice/websocket","echo", "testString").map {
//            Json.decodeValue(it, JsonRPCResultResponse::class.java)
//        }.map {
//            it.result.toString()
//        }.map {
//            testContext.assertEquals("testString", it)
//        }.setHandler(testContext.asyncAssertSuccess())
//    }

//    private fun jsonRPC(url: String, method: String, vararg params: Any?): Future<Buffer> {
//        val id = 1L
//        val result = Future.future<Buffer>()
//        try {
//            client.websocket(url) { socket ->
//                socket.handler { response ->
//                    val jo = JsonObject(response)
//                    val responseId = jo.getLong("id")
//                    if (responseId != id) {
//                        result.fail("expected id $id but $responseId")
//                    } else if (jo.containsKey("result")) {
//                        result.complete(response)
//                    } else if (jo.containsKey("error")) {
//                        result.fail(jo.getJsonObject("error").encode())
//                    } else if (jo.containsKey("completed")) {
//                        // we ignore the 'completed' message
//                    }
//                }.exceptionHandler { err ->
//                    result.fail(err)
//                }
//                val request = JsonRPCRequest(id = id, method = method, params = params.toList())
//                socket.writeFrame(WebSocketFrame.textFrame(Json.encode(request), true))
//            }
//
//
//        } catch (err: Throwable) {
//            result.fail(err)
//        }
//        return result
//    }

}

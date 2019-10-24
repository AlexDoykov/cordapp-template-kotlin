package com.template

import io.bluebank.braid.corda.BraidConfig
import io.bluebank.braid.core.logging.loggerFor
import net.corda.core.node.AppServiceHub
import net.corda.core.node.ServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import net.corda.node.services.api.ServiceHubInternal

@CordaService
class BraidServer(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    companion object {
        private val log = loggerFor<BraidServer>()
    }

    init {
        val config = BraidConfig.fromResource(configFileName)
        if (config == null) {
            log.warn("config $configFileName not found")
        } else {
            bootstrap(config)
        }
    }

    private fun bootstrap(config: BraidConfig) {
        config
                .withService("echoservice", EchoService(serviceHub))
                .bootstrapBraid(serviceHub)
    }

    private val configFileName: String
        get() {
            val name = serviceHub.myInfo.legalIdentities.first().name.organisation.replace(" ","")
            return "braid-$name.json"
        }

}

package com.template

import net.corda.core.node.ServiceHub
import net.corda.node.services.api.ServiceHubInternal

class EchoService(private val serviceHub: ServiceHub) {

    fun echo(something: String): String  {
        return something
    }

}

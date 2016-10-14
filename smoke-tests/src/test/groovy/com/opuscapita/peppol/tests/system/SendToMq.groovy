package com.opuscapita.peppol.tests.system

import com.opuscapita.peppol.tests.BaseTest
import com.opuscapita.peppol.tests.mq.MqProperties
import com.opuscapita.peppol.tests.mq.RabbitMq
import groovy.sql.Sql
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by BERZIMA1 on 11-Oct-16.
 */

class SendToRabbitMq extends MqProperties{
    @Test //publish msg to RabbitMq
    void SendToQueue() {
        def properties = MqProperties()
        def rabbitMq = RabbitMq()
        rabbitMq.send(properties, null, "persistence-test", "test")
        println(" Test 2")
    }
}

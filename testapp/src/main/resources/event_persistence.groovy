import com.opuscapita.peppol.commons.model.PeppolEvent
import com.opuscapita.peppol.commons.model.TransportType
import com.opuscapita.peppol.events.persistence.controller.PersistenceController
import com.opuscapita.peppol.events.persistence.model.CustomerRepository
import com.opuscapita.peppol.events.persistence.model.MessageRepository
import com.opuscapita.peppol.testapp.Helper

helper = binding.getProperty(Helper.NAME) as Helper

event = new PeppolEvent()
event.senderId = "000"
event.recipientId = "000"
event.fileName = "some_file.xml"
event.invoiceId = "666_777"
event.invoiceDate = "2016-04-22"
event.transportType = TransportType.IN_OUT // what the hell is that?
// more fields here

controller = helper.context.getBean(PersistenceController.class)
controller.storePeppolEvent(event)

customerRepository = helper.context.getBean(CustomerRepository.class)
messageRepository = helper.context.getBean(MessageRepository.class)

customer = customerRepository.findByIdentifier("000")

message = messageRepository.findBySenderAndInvoiceNumber(customer, "666_777")

println('------')
println message
println message.invoiceNumber
println('------')

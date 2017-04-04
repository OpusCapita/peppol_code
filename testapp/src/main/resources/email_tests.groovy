import com.opuscapita.peppol.commons.container.ContainerMessage
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument
import com.opuscapita.peppol.commons.container.process.route.Endpoint
import com.opuscapita.peppol.commons.model.Customer
import com.opuscapita.peppol.email.controller.EmailController
import com.opuscapita.peppol.email.model.CustomerRepository
import com.opuscapita.peppol.testapp.Helper

helper = binding.getProperty(Helper.NAME) as Helper

doc = new InvalidDocument("Something went wrong", null, "/tmp/testfile.xml")
doc.setDocumentId("666")
doc.setSenderId("test")
cm = new ContainerMessage(doc, "metadata", Endpoint.GATEWAY)

println cm

customerRepository = helper.context.getBean(CustomerRepository.class)
customer = new Customer()
customer.identifier = "test"
customer.outboundEmails = "a@a"
customer.inboundEmails = "b@b"
customerRepository.save(customer)

emailController = helper.context.getBean(EmailController.class)
emailController.processMessage(cm)

// uncomment to send e-mails, proper smtp server must be set
// directoryChecker = helper.context.getBean(DirectoryChecker.class)
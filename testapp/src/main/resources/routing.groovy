import com.opuscapita.peppol.commons.container.ContainerMessage
import com.opuscapita.peppol.commons.container.document.DocumentLoader
import com.opuscapita.peppol.commons.container.route.Endpoint
import com.opuscapita.peppol.testapp.Helper

Helper helper = binding.getProperty(Helper.NAME) as Helper

def doc = new DocumentLoader().load("/home/redis/peppol2.0/common/src/test/resources/valid/ehf.xml")
def cm = new ContainerMessage(doc, "test", Endpoint.PEPPOL)

println cm

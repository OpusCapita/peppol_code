import com.opuscapita.peppol.commons.container.route.Endpoint
import com.opuscapita.peppol.testapp.Helper

Helper helper = binding.getProperty(Helper.NAME) as Helper
def factory = helper.containerMessageFactory

def cm = factory.create("/home/rozeser1/peppol2.0/common/src/test/resources/valid/ehf.xml", Endpoint.PEPPOL)

println cm

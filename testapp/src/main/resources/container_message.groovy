import com.opuscapita.peppol.commons.container.document.DocumentLoader
import com.opuscapita.peppol.commons.container.document.impl.UblDocument

def cm = new DocumentLoader().load("/tmp/cm_input.xml")

print cm instanceof UblDocument


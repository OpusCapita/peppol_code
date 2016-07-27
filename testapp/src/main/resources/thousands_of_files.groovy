import com.opuscapita.peppol.commons.container.document.DocumentLoader
import com.opuscapita.peppol.commons.container.document.impl.UblDocument

def dn = "C:\\rozeser1\\files_from_lion"
def dir = new File(dn)

//println new DocumentLoader().load(dn + "\\" + "b66741f8-807f-4360-bfb9-10b0d3341668.xml").class


for (String file in dir.list()) {
    // print file + "   ->   "
    def dl = new DocumentLoader()
    def result = dl.load(dn + "\\" + file)
    // println result.getClass()
    if (result.class != UblDocument.class) {
        println file + "  ->  " + result.class
    }
}

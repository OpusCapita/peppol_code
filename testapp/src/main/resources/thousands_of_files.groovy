import com.opuscapita.peppol.commons.container.document.DocumentLoader

def dn = "C:\\rozeser1\\files_from_lion"
def dir = new File(dn)

println new DocumentLoader().load(dn + "\\" + "uuid_f3d8a35b-8ae0-4916-a330-5d0675b40b04.xml").class

/*
for (String file in dir.list()) {
    // print file + "   ->   "
    def dl = new DocumentLoader()
    def result = dl.load(dn + "\\" + file)
    // println result.getClass()
    if (result.class != UblDocument.class) {
        println file + "  ->  " + result.class
    }
}
*/
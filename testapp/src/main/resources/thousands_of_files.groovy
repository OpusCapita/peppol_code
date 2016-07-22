import com.opuscapita.peppol.commons.container.document.DocumentLoader

def dn = "C:\\rozeser1\\files_from_lion"
def dir = new File(dn)

for (String file in dir.list()) {
    print file + "   ->   "
    def dl = new DocumentLoader()
    def result = dl.load(dn + "\\" + file)
    println result.getClass()
}
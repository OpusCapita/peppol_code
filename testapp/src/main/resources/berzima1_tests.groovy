import com.opuscapita.peppol.commons.container.document.DocumentLoader
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument
import com.opuscapita.peppol.commons.container.document.impl.SveFaktura1Document
import com.opuscapita.peppol.commons.container.document.impl.UblDocument

//Svefaktura 1.0 examples:
//def dn = "C:\\PEPPOL\\test_files\\files_from_XIB-removed-sensetive-info"
//invalid examples:
//def dn = "C:\\PEPPOL\\test_files\\invalid"
//valid examples:
//def dn = "C:\\PEPPOL\\test_files\\valid"
//test sample suit:
def dn = "C:\\PEPPOL\\test_files\\suite"

def dir = new File(dn)

def count_SveFaktura1Document = 0
def count_UblDocument = 0
def count_InvalidDocument = 0
//println new DocumentLoader().load(dn + "\\" + "b66741f8-807f-4360-bfb9-10b0d3341668.xml").class

println "------------- LOGS ----------------"
for (String file in dir.list()) {
    // print file + "   ->   "
    def dl = new DocumentLoader()
    def result = dl.load(dn + "\\" + file)

    // println result.getClass()
    if (result.class == SveFaktura1Document.class) {
        println file + "SveFaktura1Document  ->  " + result.class
        count_SveFaktura1Document += 1
    }
    if (result.class == InvalidDocument.class) {
        println file + "InvalidDocument  ->  " + result.class
        count_InvalidDocument += 1
    }

    if (result.class == UblDocument.class) {
        println file + "UblDocument  ->  " + result.class
        count_UblDocument += 1
    }
}
println "------------- SUMMARY ----------------"
println  "SveFaktura1Document documents: " + count_SveFaktura1Document
println  "UblDocument documents: " + count_UblDocument
println  "InvalidDocument documents: " + count_InvalidDocument
println "--------------  END  -----------------"

assert count_SveFaktura1Document==6
assert count_UblDocument==5
assert count_InvalidDocument==3
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
//def dn = "C:\\PEPPOL\\test_files\\suite"
//Test on Linux Box:
def dn = "/home/berzima1/TestFiles/suite"

def dir = new File(dn)

def count_SveFaktura1Document = 0
def count_UblDocument = 0
def count_InvalidDocument = 0
def count_bii04_v1 = 0
def count_bii04_v2 = 0
def count_bii05 = 0
//println new DocumentLoader().load(dn + "\\" + "b66741f8-807f-4360-bfb9-10b0d3341668.xml").class
def output = "/home/berzima1/TestFiles"

println "------------- LOGS ----------------"
for (String file in dir.list()) {
    // print file + "   ->   "
    def dl = new DocumentLoader()
    //Linux Box:
    def result = dl.load(dn + "/" + file)
    //Windows:
    //def result = dl.load(dn + "\\" + file)

    // println result.getClass()
    if (result.class == SveFaktura1Document.class) {
        println file
        println "   SveFaktura1Document type  ->  " + result.profileId
        count_SveFaktura1Document += 1
    }
    if (result.class == InvalidDocument.class) {
        println file
        println  "  <- InvalidDocument"
        count_InvalidDocument += 1
    }

    if (result.class == UblDocument.class) {
        count_UblDocument += 1
        println file
        println "   UblDocument type  ->  " + result.profileId
        if (result.profileId == "urn:www.cenbii.eu:profile:bii04:ver2.0") {
            count_bii04_v1 += 1
        }
        if (result.profileId == "urn:www.cenbii.eu:profile:bii04:ver1.0") {
            count_bii04_v2 += 1
        }
        if (result.profileId == "urn:www.cenbii.eu:profile:bii05:ver2.0") {
            count_bii05 += 1
        }
    }
}
println "------------- SUMMARY ----------------"
println  "Total documents: " + (count_SveFaktura1Document+count_InvalidDocument+count_UblDocument)
println  "  - SveFaktura1Document documents: " + count_SveFaktura1Document
println  "  - UblDocument documents: " + count_UblDocument
println "       + EHF bii04_v1: " + count_bii04_v1
println "       + EHF bii04_v2: " + count_bii04_v2
println "       + EHF bii05: " + count_bii05
println  "  - InvalidDocument documents: " + count_InvalidDocument

println "--------------  END  -----------------"

//Check for automated tests:
//assert count_SveFaktura1Document==6
//assert count_UblDocument==5
//assert count_InvalidDocument==3

//write logs to file:
new File(output,'logs.txt').withWriter('utf-8') { writer ->
    writer.writeLine "------------- SUMMARY ----------------"
    writer.writeLine  "Total documents: " + (count_SveFaktura1Document+count_InvalidDocument+count_UblDocument)
    writer.writeLine  "  - SveFaktura1Document documents: " + count_SveFaktura1Document
    writer.writeLine  "  - UblDocument documents: " + count_UblDocument
    writer.writeLine "       + EHF bii04_v1: " + count_bii04_v1
    writer.writeLine "       + EHF bii04_v2: " + count_bii04_v2
    writer.writeLine "       + EHF bii05: " + count_bii05
    writer.writeLine  "  - InvalidDocument documents: " + count_InvalidDocument
    writer.writeLine "--------------  END  -----------------"
}

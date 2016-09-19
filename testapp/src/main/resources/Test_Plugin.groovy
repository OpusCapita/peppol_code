/**
 * Created by berzima1 on 9/16/16.
 */

apply plugin: GreetingPlugin
class GreetingPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('hello') << {
            println "Hello from the GreetingPlugin"
        }
    }
}
//-----------------------------
//------------------------------------------
import groovy.json.JsonSlurper
apply plugin: TestURL
class TestURL implements Plugin<Project> {
	void apply(Project project) {
		project.task('URL') << {
			String url = "http://peppol.itella.net/login"
			def json = new JsonSlurper().parseText(url.toURL().text)
			print json ['status']
		}
	}
}
//--------------------------------
//apply plugin: TestURL
//class TestURL implements Plugin<Project> {
//	void apply(Project project) {
//		project.task('URL') << {
//			//@Unroll("The url #url should throw an exception of type #exception")
//			def "exceptions can be thrown converting a String to URL and accessing the text"() {
//				when:
//				String url = "https://www.google.pl"
//				String html = url.toURL().text
//
//				then:
//				def e = thrown(exception)
//
//				where:
//				url | exception
//
//				//'htp://foo.com'              | MalformedURLException
//				//'http://google.com/notThere' | FileNotFoundException
//
//			}
//		}
//	}
//}
//--------------------------------------
//------------------------------------------
//import groovyx.net.http.HTTPBuilder
//import static groovyx.net.http.Method.GET
//import static groovyx.net.http.ContentType.TEXT
//apply plugin: TestURL2
//class TestUrl2 implements Plugin<Project> {
//	void apply(Project project) {
//		project.task('URL2') << {
//
//			def http = new HTTPBuilder()
//
//			def request = http.request('http://www.google.com', GET, TEXT) { req ->
//				uri.path = '/search'
//				uri.query = [v: '1.0', q: 'Calvin and Hobbes']
//				headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
//				headers.Accept = 'application/json'
//
//				response.success = { resp, reader ->
//					assert resp.statusLine.statusCode == 200
//					println "Got response: ${resp.statusLine}"
//					println "Content-Type: ${resp.headers.'Content-Type'}"
//					println reader.text
//				}
//
//				response.'404' = {
//					println 'Not found'
//				}
//			}
//
//
//		}
//	}
//}
//-----------------------------------------
//import java.io.*
//import groovyx.net.http.HTTPBuilder
//import groovyx.net.http.EncoderRegistry
//import static groovyx.net.http.Method.*
//import static groovyx.net.http.ContentType.*
//apply plugin: TestURL3
//class TestURL3 implements Plugin<Project> {
//	void apply(Project project) {
//		project.task('URL3') << {
//			def http = new groovyx.net.http.HTTPBuilder("http://10.117.83.21:8888/admin/health")
//
//			http.request(POST, JSON) { req ->
//				req.body {
//
//				}
//				response.success = { resp, reader ->
//					println "$resp.statusLine   Respond rec"
//
//				}
//			}
//		}
//	}
//}
//class GreetingPlugin implements Plugin<Project> {
//	void apply(Project project) {
//		project.task('hello') << {
//			println "Hello from the GreetingPlugin"
//		}
//	}
//}
//-------------------------------

//------------------------------------------
//import groovy.json.JsonSlurper
//apply plugin: TestURL4
//class TestURL4 implements Plugin<Project> {
//	void apply(Project project) {
//		project.task('URL4') << {
//			String url = "http://peppol.itella.net/login"
//			def json = new JsonSlurper().parseText(url.toURL().text)
//			print json ['status']
//		}
//	}
//}
//--------------------------------
//import groovy.util.slurpersupport.GPathResult
//import groovyx.net.http.RESTClient
//import spock.lang.*
//import groovyx.net.http.ContentType
//class InjectedRestServiceTest extends Specification {
//	@RESTWebClient
//	def client
//	def "Test Server Statuses"() {
//		when: "retrieve server status"
//		def resp1 = client.get(path : "server/status/ServerOne")
//		def resp2 = client.get(path : "server/status/ServerTwo")
//		then: "test server one response"
//		assert resp1.data.serverName.text() == "ServerOne"
//		assert resp1.data.isRunning.text() == "true"
//		then: "test server two response"
//		assert resp2.data.serverName.text() == "ServerTwo"
//		assert resp2.data.isRunning.text() == "false"
//	}
//}


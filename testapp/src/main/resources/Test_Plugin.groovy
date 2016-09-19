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

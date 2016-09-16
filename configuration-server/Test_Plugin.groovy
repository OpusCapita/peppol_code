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
#!groovy
def releaseVersion
def tag = "latest"

// module images
def config_server_image
def email_notificator_image
def events_persistence_image
def inbound_image
def internal_routing_image
def preprocessing_image
def support_ui_image
def transport_image
def validator_image

// test application images
def smoke_tests_image

// additional properties loaded from file
def properties  

def Properties loadProperties(String filename) {
    Properties properties = new Properties()
    String content = readFile "${filename}"
    properties.load(new StringReader(content));
    return properties
}

node {
    stage('Build') {
        dir('src') {
            // get latest version of code
            git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'

            // assemble modules
            sh '''
                bash gradlew clean \
                    configuration-server:assemble \
                    email-notificator:assemble \
                    events-persistence:assemble \
                    inbound:assemble \
                    internal-routing:assemble \
                    preprocessing:assemble \
                    support-ui:assemble \
                    transport:assemble \
                    validator:assemble
            '''

            // assemble smoke-tests from subdirectory since they are not part of the main project
            dir('smoke-tests') {
                sh '''
                    bash gradlew assemble
                '''
            }

            // load additional properties
            properties = loadProperties('gradle.properties')
            releaseVersion = properties.version
            tag = "${releaseVersion}-${env.BUILD_NUMBER}"
        } 
        dir('infra') {
            // get latest version of infrastructure
            git branch: 'develop', url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/infrastructure.git'
        }
    }

    stage('Unit Test') {
        dir('src') {
            // check modules
            sh '''
                bash gradlew \
                    configuration-server:check \
                    email-notificator:check \
                    events-persistence:check \
                    inbound:check \
                    internal-routing:check \
                    preprocessing:check \
                    support-ui:check \
                    transport:check \
                    validator:check
            '''
            
            // check smoke-tests from subdirectory since they are not part of the main project
            dir('smoke-tests') {
                sh 'bash gradlew check'
            }
        }
    }

    stage('Package') {
        // build docker images for the main modules
        config_server_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/configuration-server:${tag}", "src/configuration-server/")
        email_notificator_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/email-notificator:${tag}", "src/email-notificator/")
        events_persistence_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/events-persistence:${tag}", "src/events-persistence/")
        inbound_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/inbound:${tag}", "src/inbound/")
        internal_routing_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/internal-routing:${tag}", "src/internal-routing/")
        preprocessing_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/preprocessing:${tag}", "src/preprocessing/")
        support_ui_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/support-ui:${tag}", "src/support-ui/")
        transport_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/transport:${tag}", "src/transport/")
        validator_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/validator:${tag}", "src/validator/")

        // build docker images for the test modules
        smoke_tests_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/smoke-tests:${tag}", "src/smoke-tests/")
    }

    stage('Release') {
        dir('src') {
            // automatic versions trigger a new build repeatedly, disabled for now
            //sh 'bash gradlew release -Prelease.useAutomaticVersion=true'
        }
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'docker-login', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME']]) {
            sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD d-l-tools.ocnet.local:443'

            // push module images to registry
            config_server_image.push("latest")
            config_server_image.push("${tag}")
            email_notificator_image.push("latest")
            email_notificator_image.push("${tag}")
            events_persistence_image.push("latest")
            events_persistence_image.push("${tag}")
            inbound_image.push("latest")
            inbound_image.push("${tag}")
            internal_routing_image.push("latest")
            internal_routing_image.push("${tag}")
            preprocessing_image.push("latest")
            preprocessing_image.push("${tag}")
            support_ui_image.push("latest")
            support_ui_image.push("${tag}")
            transport_image.push("latest")
            transport_image.push("${tag}")
            validator_image.push("latest")
            validator_image.push("${tag}")

            // push test images to registry
            smoke_tests_image.push("latest")
            smoke_tests_image.push("${tag}")
        }
    }

    def ansible_hosts = "stage.hosts"
    stage('Deploy Stage') {
        dir('infra/ap2/ansible') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' --timeout=25 peppol-components.yml"
            }
        }
    }

    stage('Smoke Test') {
        dir('infra/ap2/ansible') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' --timeout=25 smoke-tests.yml"
            }
            archiveArtifacts artifacts: 'test/smoke-tests-results.html'
        }
    }
}


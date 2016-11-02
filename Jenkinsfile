#!groovy
def releaseVersion
def tag = "latest"

def config_server_image
def email_notificator_image
def events_persistence_image
def inbound_image
def preprocessing_image
def support_ui_image
def transport_image

def properties  // additional properties loaded from file

def Properties loadProperties(String filename) {
    Properties properties = new Properties()
    String content = readFile "${filename}"
    properties.load(new StringReader(content));
    return properties
}

node {
    stage('Build') {
        dir('src') {
            git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'
            sh '''
                bash gradlew clean \
                configuration-server:assemble \
                email-notificator:assemble \
                events-persistence:assemble \
                inbound:assemble \
                preprocessing:assemble \
                support-ui:assemble \
                transport:assemble
            '''
            properties = loadProperties('gradle.properties')
            releaseVersion = properties.version
            tag = "${releaseVersion}-${env.BUILD_NUMBER}"
        } 
        dir('infra') {
            git branch: 'develop', url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/infrastructure.git'
        }
    }

    stage('Unit Test') {
        dir('src') {
            sh '''
                bash gradlew \
                configuration-server:check \
                email-notificator:check \
                events-persistence:check \
                inbound:check \
                preprocessing:check \
                support-ui:check \
                transport:check
            '''
        }
    }

    stage('Package') {
        dir('src/configuration-server') {
            config_server_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/configuration-server:${tag}", ".")
        }
        dir('src/email-notificator') {
            email_notificator_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/email-notificator:${tag}", ".")
        }
        dir('src/events-persistence') {
            events_persistence_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/events-persistence:${tag}", ".")
        }
        dir('src/inbound') {
            inbound_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/inbound:${tag}", ".")
        }
        dir('src/preprocessing') {
            preprocessing_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/preprocessing:${tag}", ".")
        }
        dir('src/support-ui') {
            support_ui_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/support-ui:${tag}", ".")
        }
        dir('src/transport') {
            transport_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/transport:${tag}", ".")
        }
    }

    stage('Release') {
        dir('src') {
            // automatic versions trigger a new build repeatedly, disabled for now
            //sh 'bash gradlew release -Prelease.useAutomaticVersion=true'
        }
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'docker-login', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME']]) {
            sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD d-l-tools.ocnet.local:443'
            config_server_image.push("latest")
            config_server_image.push("${tag}")
            email_notificator_image.push("latest")
            email_notificator_image.push("${tag}")
            events_persistence_image.push("latest")
            events_persistence_image.push("${tag}")
            inbound_image.push("latest")
            inbound_image.push("${tag}")
            preprocessing_image.push("latest")
            preprocessing_image.push("${tag}")
            support_ui_image.push("latest")
            support_ui_image.push("${tag}")
            transport_image.push("latest")
            transport_image.push("${tag}")
        }
    }

    stage('Deploy Stage') {
        def ansible_hosts = "stage.hosts"
        dir('infra/ap2/ansible') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' --timeout=25 peppol-components.yml"
            }
        }
    }    
}



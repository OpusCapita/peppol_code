#!groovy
def tag = "latest"

node {
    stage('Build') {
        dir('src') {
            git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'
            sh '''
                bash gradlew clean \
                configuration-server:assemble \
                events-persistence:assemble \
                support-ui:assemble
            '''
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
                events-persistence:check \
                support-ui:check
            '''
        }
    }

    stage('Package') {
        dir('src/configuration-server') {
            def config_server_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/configuration-server:${tag}", ".")
        }
        dir('src/events-persistence') {
            def events-persistence_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/events-persistence:${tag}", ".")
        }
    }

    stage('Release') {
        dir('src') {
            // disabled until we get PeppolJenkins service user
            //sh 'bash gradlew release -Prelease.useAutomaticVersion=true'
        }
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'docker-login', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME']]) {
            sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD d-l-tools.ocnet.local:443'
            config_server_image.push("")
            config_server_image.push("${tag}")
            events_persistence_image.push("")
            events_persistence_image.push("${tag}")
        }
    }

    stage('Deploy Stage') {
        def ansible_hosts = "stage.hosts"
        dir('infra/ap2') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' --timeout=25 peppol-components.yml"
            }
        }
    }    
}



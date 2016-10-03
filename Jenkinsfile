#!groovy
def tag = "latest"

node {
    stage('Build') {
        dir('src') {
            git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'
            sh 'bash gradlew clean configuration-server:assemble'
            //sh 'bash gradlew configuration-server:assemble'
            //sh 'bash gradlew events-persistence:assemble'
        } 
        dir('infra') {
            git branch: 'develop', url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/infrastructure.git'
        }
    }
    stage('Unit Test') {
        dir('src') {
            sh 'bash gradlew configuration-server:check'
            //sh 'bash gradlew events-persistence:check'
        }
    }
    stage('Package') {
        dir('src/configuration-server') {
            config_server_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/configuration-server:${tag}", ".")
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'docker-login', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME']]) {
                sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD d-l-tools.ocnet.local:443'
                config_server_image.push("${tag}")
            }
        }
    }
    stage('Deploy Stage') {
        def ansible_hosts = "stage.hosts"
        dir('infra/ap2') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' peppol-components.yml"
            }
        }
    }    
}



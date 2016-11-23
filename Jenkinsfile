#!groovy
def releaseVersion
def tag = "latest"
def status = 0      // build status

def code_author
def infra_author

// module images
def config_server_image
def email_notificator_image
def events_persistence_image
def eventing_image
def inbound_image
def internal_routing_image
def outbound_image
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


@NonCPS
def getChangeString() {
    MAX_MSG_LEN = 100
    def changeString = ""

    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString?.trim()) {
        changeString = " - No new changes"
    }
    return changeString
}


// define mailing list aliases
def recipients = [:]
recipients.developers = "Kalnin Daniil <Daniil.Kalnin@opuscapita.com>, Gamans Sergejs <Sergejs.Gamans@opuscapita.com>, Roze Sergejs <Sergejs.Roze@opuscapita.com>"
recipients.devops = "Didrihsons Edgars <Edgars.Didrihsons@opuscapita.com>"
recipients.ops = "Barczykowski Bartosz <Bartosz.Barczykowski@opuscapita.com>"
recipients.testers = "Bērziņš Mārtiņš <Martins.Berzins@opuscapita.com>"

def emailNotify(String whom, String message) {
    def changes = getChangeString()

    mail to: whom, cc: recipients.devops,
        subject: "Job '${JOB_NAME}': build ${BUILD_NUMBER} has failed!",
        body: """
${message}
Please go to ${BUILD_URL} and fix the build!

Build status: ${currentBuild.result}
Build URL: ${BUILD_URL}
Project: ${JOB_NAME}
Date of build: ${currentBuild.startTimeInMillis}
Build duration: ${currentBuild.duration}

CHANGE SET
${changes}

"""
}

def failBuild(String email_recipients, message) {
    emailNotify whom: email_recipients, message: message
    error message
}

node {
    stage('Build') {
        dir('src') {
            // get latest version of code
            git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'
            code_author = sh returnStdout: true, script: 'git show -s --pretty=%ae'

            // assemble modules
            sh '''
                bash gradlew clean \
                    configuration-server:assemble \
                    email-notificator:assemble \
                    events-persistence:assemble \
                    eventing:assemble \
                    inbound:assemble \
                    internal-routing:assemble \
                    outbound:assemble \
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
            infra_author = sh returnStdout: true, script: 'git show -s --pretty=%ae'
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
                    eventing:check \
                    inbound:check \
                    internal-routing:check \
                    outbound:check \
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
        eventing_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/eventing:${tag}", "src/eventing/")
        inbound_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/inbound:${tag}", "src/inbound/")
        internal_routing_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/internal-routing:${tag}", "src/internal-routing/")
        outbound_image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/outbound:${tag}", "src/outbound/")
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
            eventing_image.push("latest")
            eventing_image.push("${tag}")
            inbound_image.push("latest")
            inbound_image.push("${tag}")
            internal_routing_image.push("latest")
            internal_routing_image.push("${tag}")
            outbound_image.push("latest")
            outbound_image.push("${tag}")
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
                sh "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD}' --timeout=25 peppol-components.yml -v"
            }
        }
    }

    stage('Smoke Test') {
        dir('infra/ap2/ansible') {
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ansible-sudo', passwordVariable: 'ANSIBLE_PASSWORD', usernameVariable: 'ANSIBLE_USERNAME']]) {
                status = sh returnStatus: true, script: "ansible-playbook -i '${ansible_hosts}' --user='${ANSIBLE_USERNAME}' --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD} provisioning=true' --timeout=25 smoke-tests.yml"
            }
            archiveArtifacts artifacts: 'test/smoke-tests-results.html'
        }
        if (status != 0) {
            failBuild notify: "${recipients.testers}, ${infra_author}, ${code_author}" message: 'Smoke tests have failed. Check the log for details.'
        }
    }
}


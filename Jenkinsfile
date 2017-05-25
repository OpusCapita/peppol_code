#!groovy

/**
 * List of modules. Each module is expected to be in subdirectory named as
 * module for assemble, check and dockerBuild to work properly.
 */
def modules = [
    [name: 'email-notificator'],
    [name: 'events-persistence'],
    [name: 'eventing'],
    [name: 'file-to-mq'],
    [name: 'inbound'],
    [name: 'internal-routing'],
    [name: 'mq-to-file'],
    [name: 'outbound'],
    [name: 'preprocessing'],
    [name: 'support-ui'],
    [name: 'validator'],

    [name: 'configuration-server'],
    [name: 'service-discovery'],
    [name: 'zuul-proxy']
]

def test_modules = [
    [name: 'integration-tests'],
    [name: 'smoke-tests']
]

/**
 * A map of recipient groups. Each group should have comma separated list of
 * email addresses to receive notifications. See 'handlers' section for details
 * on how the groups are used in certain notifications.
 */
recipients = [:]
recipients.developers = "Kalnin Daniil <Daniil.Kalnin@opuscapita.com>, Gamans Sergejs <Sergejs.Gamans@opuscapita.com>, Roze Sergejs <Sergejs.Roze@opuscapita.com>"
recipients.devops = "Didrihsons Edgars <Edgars.Didrihsons@opuscapita.com>"
recipients.ops = "Barczykowski Bartosz <Bartosz.Barczykowski@opuscapita.com>"
recipients.testers = "Bērziņš Mārtiņš <Martins.Berzins@opuscapita.com>"


def releaseVersion
def tag = "latest"
def status = 0      // build status

code_author = ""
infra_author = ""

def properties      // additional properties loaded from file

pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
        skipDefaultCheckout()
    }
    triggers { pollSCM('H/5 * * * *') }
    stages {
        stage('Checkout') {
            steps {
                dir('src') {
                    // get latest version of code
                    git 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git'
                }
                dir('infra') {
                    // get latest version of infrastructure
                    git branch: 'develop', url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/infrastructure.git'

                    // install additional roles
                    dir('ap2/ansible') {
                        sh 'make requirements'
                    }
                }

                script {
                    dir('src') {
                        code_author = sh returnStdout: true, script: 'git show -s --pretty=%ae'

                        // load additional properties
                        properties = loadProperties('gradle.properties')
                        releaseVersion = properties.version
                        tag = "${releaseVersion}-${env.BUILD_NUMBER}"
                    }
                    dir('infra') {
                        infra_author = sh returnStdout: true, script: 'git show -s --pretty=%ae'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                dir('src') {
                    sh 'bash gradlew clean assemble'
                    assemble(test_modules)
                }
            }
        }

        stage('Unit Test') {
            steps {
                dir('src') {
                    sh 'bash gradlew check'
                    check(test_modules)
                }
            }
            post {
                always {
                    junit 'src/*/build/test-results/*.xml'
                }
                failure {
                    error 'Unit tests failed for some reason'
                }
            }
        }

        stage('Package') {
            steps {
                dir('src') {
                    dockerBuild(modules + test_modules, tag)
                }
            }
        }

        stage('Release') {
            steps {
                milestone 1
                dockerPush(modules + test_modules, ['latest', releaseVersion])
                script {
                    dir('src') {
                        // automatic versions trigger a new build repeatedly, disabled for now
                        //sh 'bash gradlew release -Prelease.useAutomaticVersion=true'
                    }
                }
            }
        }

        stage('Deploy Stage') {
            steps {
                milestone 2
                dir('infra/ap2/ansible') {
                    ansiblePlaybook('peppol-components.yml', 'stage.hosts', 'ansible-sudo')
                }
            }
            post {
                failure {
                    failBuild(
                        "${recipients.ops}, ${recipients.devops}, ${infra_author}, ${code_author}",
                        'Deployment to stage environment has failed. Check the log for details.'
                    )
                }
            }
        }

        stage('Smoke Test') {
            steps {
                milestone 3
                dir('infra/ap2/ansible') {
                    ansiblePlaybook('smoke-tests.yml', 'stage.hosts', 'ansible-sudo')
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'infra/ap2/ansible/test/smoke-tests-results.html'
                }
                failure {
                    failBuild(
                        "${recipients.testers}, ${infra_author}, ${code_author}",
                        'Smoke tests have failed. Check the log for details.'
                    )
                }
            }
        }

        stage('Integration Test') {
            steps {
                echo "Coming soon.."
                script {
                    /*
                    dir('infra/ap2/ansible') {
                        ansiblePlaybook('integration-tests.yml', 'stage.hosts', 'ansible-sudo')
                        archiveArtifacts artifacts: 'test/integration-tests-results.html'
                    }
                    */
                }
            }
        }

        stage('System Tests') {
            steps {
                dir('src/system-tests') {
                    dir('reports') { deleteDir() }
                    ansiblePlaybook(
                        'inbound-tests.yml', 'stage.hosts', 'ansible-sudo',
                        'report_path=$PWD/reports/'
                    )
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'src/system-tests/reports/*.txt'
                }
                failure {
                    failBuild(
                        "${recipients.testers}, ${infra_author}, ${code_author}",
                        'System tests have failed. Check the log for details.'
                    )
                }
            }
        }

        stage('Acceptance Test') {
            when { expression {false /*params.build_type in ['major_release', 'minor_release']*/} }
            steps {
                milestone 4
                timeout(time: 3, unit: 'DAYS') {
                    input message: 'Deploy PEPPOL Access Point to production?', ok: 'Sure'
                }
            }
        }

        stage('Deploy Production') {
            when { expression {false /*params.build_type in ['major_release', 'minor_release']*/} }
            steps {
                milestone 5
                dir('infra/ap2/ansible') {
                    ansiblePlaybook('peppol-components.yml', 'production.hosts', 'ansible-sudo')
                }
            }
            post {
                failure {
                    failBuild(
                        "${recipients.devops}",
                        'Deployment to PROD environment has failed. Check the log for details immediately!'
                    )
                }
            }
        }

    }
    post {
        failure {
            emailNotify('', 'Unexpected error has occured. Check the log for details.')
        }
    }
}



/**
 * Stages
 */

def assemble(modules) {
    for (i=0; i<modules.size(); i++) {
        def module = modules[i]
        dir (module.name) {
            sh "bash ../gradlew assemble"
        }
    }
}

def check(modules) {
    for (i=0; i<modules.size(); i++) {
        def module = modules[i]
        dir (module.name) {
            sh "bash ../gradlew check"
        }
    }
}

def dockerBuild(modules, tag) {
    for (i=0; i<modules.size(); i++) {
        def module = modules[i]
        dir (module.name) {
            module.image = docker.build("d-l-tools.ocnet.local:443/peppol2.0/${module.name}:${tag}", ".")
        }
    }
}

def dockerPush(modules, tags) {
    def docker_credentials = [[
        $class: 'UsernamePasswordMultiBinding',
        credentialsId: 'docker-login',
        usernameVariable: 'DOCKER_USERNAME',
        passwordVariable: 'DOCKER_PASSWORD'
    ]]

    withCredentials(docker_credentials) {
        sh 'docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD d-l-tools.ocnet.local:443'
    }

    for (i=0; i<modules.size(); i++) {
        def module = modules[i]
        for (j=0; j<tags.size(); j++) {
            def tag = tags[j]
            module.image.push(tag)
        }
    }
}


// execute ansible playbook on hosts using the credentials provided
def ansiblePlaybook(playbook, hosts, credentials, extraVars='') {
    def ansible_credentials = [[
        $class: 'UsernamePasswordMultiBinding',
        credentialsId: credentials,
        passwordVariable: 'ANSIBLE_PASSWORD',
        usernameVariable: 'ANSIBLE_USERNAME'
    ]]

    withCredentials(ansible_credentials) {
        sh script: """
            ansible-playbook -i '${hosts}' '${playbook}' \
            --user='${ANSIBLE_USERNAME}' \
            --extra-vars 'ansible_sudo_pass=${ANSIBLE_PASSWORD} ${extraVars}'
        """
    }
}


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
Build duration: ${currentBuild.durationString}

CHANGE SET
${changes}

"""
}


def failBuild(String email_recipients, String message) {
    emailNotify(email_recipients, message)
    error message
}

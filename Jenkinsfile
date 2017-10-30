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
    [name: 'mlr-reporter'],
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


import java.util.regex.*

def code_version = params.CODE_BRANCH ?: 'master'
def infra_version = params.INFRA_BRANCH ?: 'develop'
def release_type = params.RELEASE_TYPE ?: 'development'
def release_version, next_version, code_hash, infra_hash


try {
    node {
        stage('Checkout') {
            // clean up the last time
            dir('src/system-tests/reports') { deleteDir() }

            // get latest version of code
            checkout([
                $class: 'GitSCM',
                branches: [[name: code_version]],
                userRemoteConfigs: [[url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/peppol2.0.git']],
                extensions: [
                    [$class: 'RelativeTargetDirectory', relativeTargetDir: 'src'],
                    [$class: 'LocalBranch', localBranch: 'master']
                ]
            ])
            // get latest version of infrastructure
            checkout([
                $class: 'GitSCM',
                branches: [[name: infra_version]],
                userRemoteConfigs: [[url: 'http://nocontrol.itella.net/gitbucket/git/Peppol/infrastructure.git']],
                extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'infra']]
            ])

            // work around missing tuple handlings (https://issues.jenkins-ci.org/browse/JENKINS-38846)
            def versions = getReleaseVersion(loadProperties('src/gradle.properties').version, release_type).split('##')
            release_version = versions[0]
            next_version = versions[1]
            code_hash = getGitCommitID('src')
            infra_hash = getGitCommitID('infra')
            code_author = getGitCommitAuthor('src')
            infra_author = getGitCommitAuthor('infra')

            // install additional roles
            dir('infra/ap2/ansible') {
                sh 'make requirements'
            }
        }
        milestone 1
        stage('Build') {
            dir('src') {
                sh 'bash gradlew clean assemble'
                assemble(test_modules)
            }
        }
        stage('Unit Tests') {
            try {
                dir('src') {
                    sh 'bash gradlew check'
                    check(test_modules)
                }
            }
            catch(e) {
                error 'Unit tests failed for some reason'
            }
            finally {
                junit 'src/*/build/test-results/*.xml'
            }
        }
        stage('Package') {
            dir('src') {
                dockerBuild(modules + test_modules, release_version)
            }
        }
        milestone 2
        stage('Release') {
            dockerPush(modules + test_modules, ['latest', release_version])

            // skip versioning and tagging for development builds
            if (release_type in ['patch_release', 'minor_release', 'major_release']) {
                dir('src') {
                    release(release_version, next_version, code_version, code_hash, infra_version, infra_hash)
                }
            }
        }
    }
    node {
        lock(resource: 'peppol-stage-servers') {
            milestone 3
            stage('Integration Tests') {
                try {
                    dir('infra/ap2/ansible') {
                        ansiblePlaybook('integration-tests.yml', 'stage-integration.hosts', 'ansible-sudo', "peppol_version=${release_version}")
                    }
                } catch(e) {
                    failBuild(
                        "${recipients.testers}, ${infra_author}, ${code_author}",
                        'Integration tests have failed. Check the log for details.'
                    )
                }
                finally {
                    archiveArtifacts artifacts: 'infra/ap2/ansible/test/integration-tests-results.html'
                    dir('infra/ap2/ansible') {
                        // clean up the integration-tests environment (destroy everything)
                        ansiblePlaybook('integration-tests-clean.yml', 'stage-integration.hosts', 'ansible-sudo', "peppol_version=${release_version}")
                    }
                }
            }
            milestone 4
            stage('Deployment to Stage') {
                try {
                    dir('infra/ap2/ansible') {
                        ansiblePlaybook('peppol-components.yml', 'stage.hosts', 'ansible-sudo', "peppol_version=${release_version}")
                    }
                }
                catch(e) {
                    failBuild(
                        "${recipients.ops}, ${recipients.devops}, ${infra_author}, ${code_author}",
                        'Deployment to Stage environment has failed. Check the log for details.'
                    )
                }
            }
            stage('Smoke Tests') {
                try {
                    dir('infra/ap2/ansible') {
                        ansiblePlaybook('smoke-tests.yml', 'stage.hosts', 'ansible-sudo', "peppol_version=${release_version}")
                    }
                }
                catch(e) {
                    failBuild(
                        "${recipients.testers}, ${infra_author}, ${code_author}",
                        'Smoke tests have failed. Check the log for details.'
                    )
                }
                finally {
                    archiveArtifacts artifacts: 'infra/ap2/ansible/test/smoke-tests-results.html'
                }
            }
            stage('Acceptance') {
                if (release_type in ['patch_release', 'minor_release', 'major_release']) {
                    input message: 'Deploy PEPPOL Access Point to production?', ok: 'Sure'
                }
            }                                                                                    
        }

        lock(resource: 'peppol-production-servers') {
            milestone 5
            stage('Deployment to Production') {
                if (release_type in ['patch_release', 'minor_release', 'major_release']) {
                    try {
                        dir('infra/ap2/ansible') {
                            ansiblePlaybook('peppol-components.yml', 'production.hosts', 'ansible-sudo', "peppol_version=${release_version}")
                        }
                    }
                    catch(e) {
                        failBuild(
                            "${recipients.devops}",
                            'Deployment to PROD environment has failed. Check the log for details immediately!'
                        )
                    }
                }
            }
        }
        milestone 6
    }
}
catch(e) {
    echo e.toString()
    emailNotify('', "Unexpected error has occured. Check the log for details.\n${e}\n")
    throw e
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

def release(release_version, next_version, code_version, code_hash, infra_version, infra_hash) {
    def changes = getChangeString(250)
    def description = ""
    description += "Release: ${release_version}\n"
    description += "Code: ${code_version}" + (code_hash.startsWith(code_version) ? "" : " (${code_hash.take(7)})") + "\n"
    description += "Infra: ${infra_version}" + (infra_hash.startsWith(infra_version) ? "" : " (${infra_hash.take(7)})") + "\n"
    description += "Changes:\n"
    description += "${changes}"
    currentBuild.description = description

    sh "bash gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=${release_version} -Prelease.newVersion=${next_version}"
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


@NonCPS
def getChangeString(MAX_MSG_LEN=100) {
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

def getGitCommitID(path) {
    dir(path) {
        def commit_id = sh returnStdout: true, script: 'git rev-parse HEAD'
        return commit_id
    }
}

def getGitCommitAuthor(path) {
    dir(path) {
        def author_email = sh returnStdout: true, script: 'git show -s --pretty=%ae'
        return author_email
    }
}

def Properties loadProperties(String filename) {
    Properties properties = new Properties()
    String content = readFile "${filename}"
    properties.load(new StringReader(content));
    return properties
}

def getReleaseVersion(this_version, release_type) {
    def parser = ~/(?<major>\d+).(?<minor>\d+).(?<patch>\d+)(-(?<build>SNAPSHOT))?/
    def major, minor, patch, build
    def release_version, next_version

    def match = this_version =~ parser
    if(match.matches()) {
        major = match.group('major') as Integer
        minor = match.group('minor') as Integer
        patch = match.group('patch') as Integer
        build = match.group('build')

        // figure out the release number based on simple semver rules
        switch (release_type) {
            case 'patch_release':
                // patch number was already increased the last time we were here
                break
            case 'minor_release':
                patch = 0
                minor += 1
                break
            case 'major_release':
                // we should not have these for the foreseeable future but
                // for the sake of completeness
                patch = 0
                minor = 0
                major += 1
                break
            case 'development':
                build = env.BUILD_NUMBER
                break
            default:
                error "Unknown release type: ${release_type}"
                break
        }
        release_version = [major, minor, patch].join('.')
        if (release_type == 'development') {
            release_version += "-${build}"
        }

        // assume next version is to be bugfix/patch release
        patch += 1
        next_version = [major, minor, patch].join('.')
        next_version += '-SNAPSHOT'

        return [release_version, next_version].join('##')
    }
    error "Could not parse the version string: ${this_version}"
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

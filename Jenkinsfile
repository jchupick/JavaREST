pipeline {
    agent any
    environment {
        GITBRANCH       = 'main'
        GITHUBBASEURL   = "https://github.com"
        GITORGNAME_1    = 'jchupick'
        GITREPONAME_1   = 'JavaREST'
        GITORGNAME_2    = 'jchupick'
        GITREPONAME_2   = 'CurlTesting'
        GITHUBFULLURL_1 = "${GITHUBBASEURL}/${GITORGNAME_1}/${GITREPONAME_1}.git"
        GITHUBFULLURL_2 = "${GITHUBBASEURL}/${GITORGNAME_2}/${GITREPONAME_2}.git"

        SSH_CMD = "ssh -o StrictHostKeyChecking=no"
    }
    stages {
        stage('Git Pull Development Code') {
            steps {
                script {
                    echo "Git branch is ${GITBRANCH}" 
                }
                ws("workspace/${env.JOB_NAME}/DevCode") {
                    git branch: env.GITBRANCH,
                    url: env.GITHUBFULLURL_1
                }
            }
        }
        stage('Build') {
            steps {
                ws("workspace/${env.JOB_NAME}/DevCode") {
                    withAnt(installation: 'LocalAnt') {
                        sh "ant deploy"
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                ws("workspace/${env.JOB_NAME}/DevCode") {
                    script {
                        sh "cp -fp novels.war ../../../webapps/"
                    }
                }
            }
        }
        stage('Git Pull Testing Code') {
            steps {
                script {
                    echo "Git branch is ${GITBRANCH}" 
                }
                ws("workspace/${env.JOB_NAME}/TestCode") {
                    git branch: env.GITBRANCH,
                    url: env.GITHUBFULLURL_2
                }
            }
        }
        stage('Wait for TomCat Refresh') {
            steps {
                sleep 30
            }
        }
        stage('UAT') {
            steps {
                ws("workspace/${env.JOB_NAME}/TestCode") {
                    script {
                        sh "curl 172.18.0.4:8080/novels/"
                    }
                }
            }
        }
    }
}

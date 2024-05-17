#!groovy


def call() {

    pipeline {
        agent{ label "${workNode}"}

        environment {
            http_proxy="${params.proxyUrl}"
            https_proxy="${params.proxyUrl}"
        }
        stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: "${params.ansibleBranchName}"]], extensions: [], userRemoteConfigs: [[credentialsId: '9a922e65-7688-4308-8932-30a2672cb697', url: "${params.ansibleFileRepo}"]])
            }
        }
        stage('App deploy') {
            steps {
                script {
                    sh 'ip a| pwd'
                    def tmpProjectName=JOB_NAME.tokenize('/')[2]
                    ansiblePlaybook extras: "${params.extras}", inventory: "applications/$tmpProjectName/ansible/${params.inventoryFileName}", playbook: "applications/$tmpProjectName/ansible/${params.playBookName}"
                }
            }
        }
    }
}
    }
call()

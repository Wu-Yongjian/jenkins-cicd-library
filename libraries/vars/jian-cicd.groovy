def call() {

    pipeline {
        agent{label 'master'}
        environment {
            http_proxy="${params.proxyUrl}"
            https_proxy="${params.proxyUrl}"
        }

        stages {

            stage('Build app package') {
                agent {
                    docker {
                        image "${params.imageName}"
                        label "${params.workNode}"
                        reuseNode true

                    }}
                steps {
                    script {
                        checkout scmGit(branches: [[name: "${params.appBranchName}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f698feb7-d4c0-4c67-8c52-16ec4ad21eab', url: "${params.appRepoUrl}"]])
                        if (params.snoarCheck) {
                            withSonarQubeEnv('jian-sonarqube') { sh "mvn clean compile sonar:sonar" }
                        }
                        sh "${buildCommand}"
                    }
                }
            }

            stage('upload') {
                steps {
                    script {
                        def server = Artifactory.server "jian-frog"
                        def uploadSpec = """{
                    "files": [{
                       "pattern": "target/*.jar",
                       "target": "${params.artifactoryRepo}"
                    }]
                 }"""
                        server.upload(uploadSpec)
                    }
                }
            }
            stage('Build application image') {
                steps{
                    script{
                        def pkgName=sh(script: "echo `ls ./target/*.jar` |awk -F'/' '{print \$3}'", returnStdout: true).trim()
                        checkout scmGit(branches: [[name: "${params.appDockerFileBranchName}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f698feb7-d4c0-4c67-8c52-16ec4ad21eab', url: "${params.appDockerFileRepoUrl}"]])
                        def tmpProjectName= "${JOB_NAME}".tokenize('/')[2]
                        dir("applications/${tmpProjectName}/docker") {sh "pwd && cp ../../../target/${pkgName} ./ &&pwd && ls "
                            docker.withRegistry("${params.dockerImageRepo}", 'jian-harbor') {
                                def dockerImage = docker.build("${params.dockerImageRepo}".tokenize('/')[1] + "/" + "${JOB_NAME}".tokenize('/')[2] + "/" + "${params.appImageName}", '.')
                                dockerImage.push()
                            }
                        }
                    }
                }
            }
            stage('build trivy image') {
                when {expression { params.trivyCheck == true }}
                steps{
                    script{
                        checkout scmGit(branches: [[name: "${params.trivyBranchName}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f698feb7-d4c0-4c67-8c52-16ec4ad21eab', url: "${params.trivyRepoUrl}"]])
                        dir('tools/trivy/dockerfile') {
                            sh "pwd && ls "
                            docker.build("trivy:${trivyVersion}","--build-arg trivyVersion=${trivyVersion} .")
                            jianLog.info("trivy build done")
                        }
                    }
                }
            }
            stage('trivy scan the application  image') {
                when {expression { params.trivyCheck == true }}
                steps{
                    script{
                        jianLog.info("trivy scan begin")
                        def tmpname= params.dockerImageRepo.tokenize('/')[1] + "/" + JOB_NAME.tokenize('/')[2] +  "/"+ params.appImageName
                        sh "docker run -v /tmp:/tmp --rm trivy:${trivyVersion}  \
                        image $tmpname \
                        --severity HIGH,CRITICAL --format template --template '@contrib/html.tpl' \
                        --output /tmp/trivy_report.html --skip-db-update --insecure --timeout 20m"
                        jianLog.info("trivy scan done")

                    }


                }
            }
            stage('Push report') {
                when {expression { params.trivyCheck == true }}
                steps {
                    publishHTML(target: [
                            allowMissing: true,
                            alwaysLinkToLastBuild: false,
                            keepAll: true,
                            reportDir: "/tmp",
                            reportFiles: "trivy_report.html",
                            reportName: "Trivy Report",
                            includes: '**/*.html',
                            reportTitles: 'Trivy Scan'
                    ])
                }
            }
            stage('Prepare deploy') {
                steps {
                    checkout scmGit(branches: [[name: "${params.ansibleBranchName}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f698feb7-d4c0-4c67-8c52-16ec4ad21eab', url: "${params.ansibleFileRepo}"]])
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

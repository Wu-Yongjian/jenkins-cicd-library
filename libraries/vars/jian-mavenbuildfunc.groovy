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
            //            args '-v /opt/nfs/jenkinsdata/workspace/.m2:/root/.m2 -v /opt/nfs/jenkinsdata/workspace/java/:/usr/src/java -w /usr/src/java '
///opt/nfs/jenkinsdata/ 是在jenkins中设置的主目录所以下载的所有文件都会放到/opt/nfs/jenkinsdata/workspace/ ，该目录会自动由jenkins挂载到docker
             //          args '-v /opt/nfs/jenkinsdata/workspace/.m2:/root/.m2 '
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
 
         stage('upload the package') {
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
                        }
                    }
                }
            }
         stage('trivy scan the application  image') {
             when {expression { params.trivyCheck == true }}
             steps{
                 script{
                     jianLog.info "trivy scan"
                     def tmpname= params.dockerImageRepo.tokenize('/')[1] + "/" + JOB_NAME.tokenize('/')[2] +  "/"+ params.appImageName
                     sh "docker run -v /tmp:/tmp --rm trivy:${trivyVersion}  \
                        image $tmpname \
                        --severity HIGH,CRITICAL --format template --template '@contrib/html.tpl' \
                        --output /tmp/trivy_report.html --skip-db-update --insecure --timeout 20m"
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
        }
    }
}
call()

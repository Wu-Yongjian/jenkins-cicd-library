pipeline{
    agent{
        label 'master'
    }
    parameters{
        booleanParam(name: 'onlyUpdatePipeline')
        string(name: 'aaa', defaultValue: "",description:'')
        booleanParam(name: 'ifjobA')
        booleanParam(name: 'ifjobB')
    }
    stages{
        stage('prepare'){
            when {expression {"$onlyUpdatePipeline"}}
            steps{
                script{
                    echo "this time we only update the pipeline"
                }
            }
        }
        stage('JobA'){
            when {expression { return( params.ifjobA  &&  !params.onlyUpdatePipeline ) }}
            steps{
                script {
                    def jobBuild = build job: 'jian-devops/testjob', propagate: false
                    def jobResult = jobBuild.getResult()
                    echo "Build of 'testJob' returned result: ${jobResult}"
                }
            }
        }
        stage('JobB'){
            when {expression { return( params.ifjobB  &&  !params.onlyUpdatePipeline ) }}
            steps{
                script {
                    def jobBuild = build job: 'testjob2',parameters: [string(name: 'aaa', value: "$aaa")],propagate: false
                    def jobResult = jobBuild.getResult()
                    echo "Build of 'testJob2' returned result: ${jobResult}"
                }
            }
        }

    }
}
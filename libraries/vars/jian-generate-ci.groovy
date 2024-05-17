#!groovy
def call(messagein){
        def buildType = "${messagein.buildType}".trim()
        node{
            stage("judge the ci build  type")
               if(buildType=='maven'){
                        jianLog.info('maven build start')
                        jian-mavenbuildfunc()
                }
               else if (jobType=='cd') {
                        jianLog.info('cd start')
                         jian-cd-generate()
                }
               else if (jobType=='cicd'){
                        jianLog.info('cicd start')
                         jian-cicd-generate()
                }
               else { jianLog.info('wrong jobType do not start')}
}
}
call()

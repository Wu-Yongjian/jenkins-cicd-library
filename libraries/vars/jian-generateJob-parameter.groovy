#!groovy   
library 'jian-cicd-library'
def call(){
	jianLog.info('ci prepare')
	node{
	    stage("generate job")
		if(jobType=='ci'){
                        jianLog.info('ci job prepare')
			jobDsl scriptText: """
                        	folder ("jian-devops/ci/${params.projectName}") {
                            		description("For ${params.projectName} Team")
                                }
			
				pipelineJob ("jian-devops/ci/${params.projectName}/ci-${params.codeBuildType}-param") {
                                parameters {
                                    stringParam('imageName', '', 'Please provide image Name')
				    stringParam('workNode', '', 'Please provide workNode Name')
				    stringParam('proxyUrl', '', 'Please provide proxyUrl')
				    stringParam('appRepoUrl', '', 'Please provide code repoUrl')
				    stringParam('appBranchName', '', 'Please provide Application Branch Name')
				    booleanParam('snoarCheck', true, 'Whether to run snoarCheck or not')
				    stringParam('buildCommand', 'mvn clean package', 'Please provide build command')
				    stringParam('appImageName', '', 'Please provide appImageName ')
				    stringParam('dockerImageRepo', '', 'Please provide dockerImageRepo ')
				    stringParam('artifactoryRepo', '', 'Please provide artifactoryRepo ')
				    stringParam('appDockerFileBranchName', '', 'Please provide appDockerFileBranchName ')
				    stringParam('appDockerFileRepoUrl', '', 'Please provide appDockerFileRepoUrl ')
				    booleanParam('trivyCheck', true, 'Whether to run trivyCheck or not')
				    stringParam('trivyBranchName', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')
				    stringParam('trivyRepoUrl', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')
				    stringParam('trivyVersion', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')				    
                                }
                                definition {
        				cpsScm {
            					scm {
                					git {
                    						remote {
                        						credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						github('Wu-Yongjian/cicd-library', 'https',  'github.com')
                                                                       }
                                                            }
                                                     }
            					scriptPath('libraries/vars/jian-${params.codeBuildType}buildfunc.groovy')
                                               }
                                            }
                                 }
                    """	       
                }
               else if (jobType=='cd') {
                        jianLog.info('cd job prepare')
                      	jobDsl scriptText: """
                        	folder ("jian-devops/cd/${params.projectName}") {
                            		description("For ${params.projectName} Team")
                                }

				            pipelineJob ("jian-devops/cd/${params.projectName}/cd-param") {
                                parameters {
				                    stringParam('workNode', '', 'Please provide workNode Name')
				                    stringParam('proxyUrl', '', 'Please provide proxyUrl')
				                    stringParam('ansibleFileRepo', '', 'Please provide ansibleFileRepo repoUrl')
				                    stringParam('ansibleBranchName', '', 'Please provide ansible file Branch Name')
				                    stringParam('playBookName', '', 'Please provide playBookName ')
				                    stringParam('inventoryFileName', '', 'Please provide inventoryFileName ')
				                    stringParam('extras', '-v', 'Please provide extras ')
                                }
                                definition {
        				            cpsScm {
            					        scm {
                					            git {
                    						            remote {
                        						            credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						            github('Wu-Yongjian/cicd-library', 'https',  'github.com')
                                                                       }
                                                            }
                                                     }
            					        scriptPath('libraries/vars/jian-ansible.groovy')
                                               }
                                            }
                                 }
                    """
                }
               else if (jobType=='cicd'){
                        jianLog.info('cicd job prepare')
                        jobDsl scriptText: """
                        	folder ("jian-devops/cicd/${params.projectName}") {
                            		description("For ${params.projectName} Team")
                                }

				            pipelineJob ("jian-devops/cicd/${params.projectName}/cicd-param") {
                                parameters {
				                    stringParam('workNode', '', 'Please provide workNode Name')
				                    stringParam('proxyUrl', '', 'Please provide proxyUrl')
				                    stringParam('appRepoUrl', '', 'Please provide code repoUrl')
                                    stringParam('appBranchName', '', 'Please provide Application Branch Name')
                                    stringParam('imageName', '', 'Please provide image Name')
                                    booleanParam('snoarCheck', true, 'Whether to run snoarCheck or not')
                                    stringParam('buildCommand', 'mvn clean package', 'Please provide build command')
                                    stringParam('appImageName', '', 'Please provide appImageName ')
                                    stringParam('dockerImageRepo', '', 'Please provide dockerImageRepo ')
                                    stringParam('artifactoryRepo', '', 'Please provide artifactoryRepo Name')
                                    stringParam('appDockerFileBranchName', '', 'Please provide appDockerFileBranchName ')
                                    stringParam('appDockerFileRepoUrl', '', 'Please provide appDockerFileRepoUrl ')
                                    booleanParam('trivyCheck', true, 'Whether to run trivyCheck or not')
                                    stringParam('trivyBranchName', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')
                                    stringParam('trivyRepoUrl', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')
                                    stringParam('trivyVersion', '', 'Please provide trivyBranchName Name if no trivycheck then no need input')
				                    stringParam('ansibleFileRepo', '', 'Please provide ansibleFileRepo repoUrl')
				                    stringParam('ansibleBranchName', '', 'Please provide ansible file Branch Name')
				                    stringParam('playBookName', '', 'Please provide playBookName ')
				                    stringParam('inventoryFileName', '', 'Please provide inventoryFileName ')
				                    stringParam('extras', '-v', 'Please provide extras ')
                                }
                                definition {
        				            cpsScm {
            					        scm {
                					            git {
                    						            remote {
                        						            credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						            github('Wu-Yongjian/cicd-library', 'https',  'github.com')
                                                                       }
                                                            }
                                                     }
            					        scriptPath('libraries/vars/jian-cicd.groovy')
                                               }
                                            }
                                 }
                    """
                }
               else { jianLog.info('wrong jobType do not start')}
}
}
call()

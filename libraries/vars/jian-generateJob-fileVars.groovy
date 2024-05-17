#!groovy   
library 'jian-cicd-library'
def call(){
	jianLog.info('job prepare')
	node{
	    stage("generate job")
		if("${params.jobType}"=='ci'){
            		jianLog.info('ci pipeline enter.......')
			git url: "${params.varFileRepo}", credentialsId: '9a922e65-7688-4308-8932-30a2672cb697',branch:"${params.varFileRepoBranch}"
            		filename = sh (script: """  git ls-tree --full-tree -r --name-only HEAD|egrep -iv ".gitignore|default|readme.md|.groovy" |grep ${params.varFileName} """, returnStdout: 'True').trim().split('\n')
            		jianLog.info "got files ${filename}"
            		def jobPipelineVariables = readYaml file: "${filename[0]}"
            		jianLog.info "jobPipelineVariables ${jobPipelineVariables}"

			jobDsl scriptText: """
                		folder ("jian-devops/ci/${params.projectName}") {
                    			description("For ${params.projectName} Team")
                                }
			
				pipelineJob ("jian-devops/ci/${params.projectName}/ci-${jobPipelineVariables['ci'].buildType[0]}-fileVars") {
                  			parameters {
                    				stringParam('imageName', "${jobPipelineVariables['ci'].imageName[0]}", 'Please provide image Name')
				    		stringParam('workNode', "${jobPipelineVariables['ci'].workNode[0]}", 'Please provide workNode Name')
				    		stringParam('proxyUrl', "${jobPipelineVariables['ci'].proxyUrl[0]}", 'Please provide proxyUrl')
				    		stringParam('appRepoUrl', "${jobPipelineVariables['ci'].appRepoUrl[0]}", 'Please provide code repoUrl')
				    		stringParam('appBranchName', "${jobPipelineVariables['ci'].appBranchName[0]}", 'Please provide Application Branch Name')
                            stringParam('buildType',"${jobPipelineVariables['ci'].buildType[0]}", 'Please provide buildType Name')
				    		stringParam('buildCommand', "${jobPipelineVariables['ci'].buildCommand[0]}", 'Please provide build command')
                            booleanParam('snoarCheck', "${jobPipelineVariables['ci'].snoarCheck[0]}".toBoolean(), 'Whether to run snoarCheck or not')
				    		stringParam('artifactoryRepo',"${jobPipelineVariables['ci'].artifactoryRepo[0]}", 'Please provide artifactoryRepo Name')
                            stringParam('appDockerFileBranchName',"${jobPipelineVariables['ci'].appDockerFileBranchName[0]}", 'Please provide appDockerFileBranchName Name')
                            stringParam('appDockerFileRepoUrl',"${jobPipelineVariables['ci'].appDockerFileRepoUrl[0]}", 'Please provide appDockerFileRepoUrl Name')				    
                            stringParam('appImageName', "${jobPipelineVariables['ci'].appImageName[0]}", 'Please provide appImageName ')
                            stringParam('dockerImageRepo', "${jobPipelineVariables['ci'].dockerImageRepo[0]}", 'Please provide dockerImageRepo ')                            
                            booleanParam('trivyCheck', "${jobPipelineVariables['ci'].trivyCheck[0]}".toBoolean(), 'Whether to run trivyCheck or not')
                            stringParam('trivyBranchName',"${jobPipelineVariables['ci'].trivyBranchName[0]}", 'Please provide trivyBranchName Name')
                            stringParam('trivyRepoUrl',"${jobPipelineVariables['ci'].trivyRepoUrl[0]}", 'Please provide trivyRepoUrl Name')
                            stringParam('trivyVersion',"${jobPipelineVariables['ci'].trivyVersion[0]}", 'Please provide trivyVersion Name')

			    
                                }
                                definition {
        				cpsScm {
            					scm {
                					git {
                    						remote {
                        						credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						github('Wu-Yongjian/jenkins-cicd-library', 'https',  'github.com')
                                                                       }
                                                            }
                                                     }
            					scriptPath('libraries/vars/jian-${jobPipelineVariables['ci'].buildType[0]}buildfunc.groovy')
                                               }
                                            }
                                 }
                    """	       
                }
               else if (jobType=='cd') {
                        jianLog.info('cd pipeline enter.....')
                        git url: "${params.varFileRepo}", credentialsId: '9a922e65-7688-4308-8932-30a2672cb697',branch:"${params.varFileRepoBranch}"
                        filename = sh (script: """  git ls-tree --full-tree -r --name-only HEAD|egrep -iv ".gitignore|default|readme.md|.groovy" |grep ${params.varFileName} """, returnStdout: 'True').trim().split('\n')
                        jianLog.info "got files ${filename}"
                        def jobPipelineVariables = readYaml file: "${filename[0]}"
                        jianLog.info "jobPipelineVariables ${jobPipelineVariables}"

                        jobDsl scriptText: """
                            	folder ("jian-devops/cd/${params.projectName}") {
                                	description("For ${params.projectName} Team")
                                	}
			
				pipelineJob ("jian-devops/cd/${params.projectName}/cd-fileVars") {
                                	parameters {
				               	stringParam('workNode', "${jobPipelineVariables['cd'].workNode[0]}", 'Please provide workNode Name')
				             	stringParam('proxyUrl', "${jobPipelineVariables['cd'].proxyUrl[0]}", 'Please provide proxyUrl')
				             	stringParam('ansibleFileRepo', "${jobPipelineVariables['cd'].ansibleFileRepo[0]}", 'Please provide code repoUrl')
				             	stringParam('ansibleBranchName', "${jobPipelineVariables['cd'].ansibleBranchName[0]}", 'Please provide Application Branch Name')
				             	stringParam('playBookName', "${jobPipelineVariables['cd'].playBookName[0]}", 'Please provide build command')
                                    		stringParam('inventoryFileName', "${jobPipelineVariables['cd'].inventoryFileName[0]}", 'Please provide build command')
                                    		stringParam('extras', "${jobPipelineVariables['cd'].extras[0]}", 'Please provide build command')
                                		}
                                	definition {
        				                cpsScm {
            					                scm {
                					                git {
                    						                remote {
                        						                credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						                github('Wu-Yongjian/jenkins-cicd-library', 'https',  'github.com')
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
                        jianLog.info('cicd pipeline enter.....')
                        git url: "${params.varFileRepo}", credentialsId: '9a922e65-7688-4308-8932-30a2672cb697',branch:"${params.varFileRepoBranch}"
                        filename = sh (script: """  git ls-tree --full-tree -r --name-only HEAD|egrep -iv ".gitignore|default|readme.md|.groovy" |grep ${params.varFileName} """, returnStdout: 'True').trim().split('\n')
                        jianLog.info "got files ${filename}"
                        def jobPipelineVariables = readYaml file: "${filename[0]}"
                        jianLog.info "jobPipelineVariables ${jobPipelineVariables}"

                        jobDsl scriptText: """
                            	folder ("jian-devops/cicd/${params.projectName}") {
                                	description("For ${params.projectName} Team")
                                	}
			
				                pipelineJob ("jian-devops/cicd/${params.projectName}/cicd-fileVars") {
                                	parameters {
				               	            stringParam('workNode', "${jobPipelineVariables['ci'].workNode[0]}", 'Please provide workNode Name')
				             	            stringParam('proxyUrl', "${jobPipelineVariables['ci'].proxyUrl[0]}", 'Please provide proxyUrl')
                                            stringParam('imageName', "${jobPipelineVariables['ci'].imageName[0]}", 'Please provide image Name')
                                            stringParam('appRepoUrl', "${jobPipelineVariables['ci'].appRepoUrl[0]}", 'Please provide code repoUrl')
                                            stringParam('appBranchName', "${jobPipelineVariables['ci'].appBranchName[0]}", 'Please provide Application Branch Name')
                                            stringParam('buildCommand', "${jobPipelineVariables['ci'].buildCommand[0]}", 'Please provide build command')
                                            booleanParam('snoarCheck', "${jobPipelineVariables['ci'].snoarCheck[0]}".toBoolean(), 'Whether to run snoarCheck or not')
                                            stringParam('artifactoryRepo',"${jobPipelineVariables['ci'].artifactoryRepo[0]}", 'Please provide artifactoryRepo Name')
                                            stringParam('appDockerFileBranchName',"${jobPipelineVariables['ci'].appDockerFileBranchName[0]}", 'Please provide appDockerFileBranchName Name')
                                            stringParam('appDockerFileRepoUrl',"${jobPipelineVariables['ci'].appDockerFileRepoUrl[0]}", 'Please provide appDockerFileRepoUrl Name')   
                                            stringParam('buildType',"${jobPipelineVariables['ci'].buildType[0]}", 'Please provide buildType Name')
                                            stringParam('appImageName', "${jobPipelineVariables['ci'].appImageName[0]}", 'Please provide appImageName ')
                                            stringParam('dockerImageRepo', "${jobPipelineVariables['ci'].dockerImageRepo[0]}", 'Please provide dockerImageRepo ')                            
                                            booleanParam('trivyCheck', "${jobPipelineVariables['ci'].trivyCheck[0]}".toBoolean(), 'Whether to run trivyCheck or not')
                                            stringParam('trivyBranchName',"${jobPipelineVariables['ci'].trivyBranchName[0]}", 'Please provide trivyBranchName Name')
                                            stringParam('trivyRepoUrl',"${jobPipelineVariables['ci'].trivyRepoUrl[0]}", 'Please provide trivyRepoUrl Name')
                                            stringParam('trivyVersion',"${jobPipelineVariables['ci'].trivyVersion[0]}", 'Please provide trivyVersion Name')
				             	            stringParam('ansibleFileRepo', "${jobPipelineVariables['cd'].ansibleFileRepo[0]}", 'Please provide code repoUrl')
				             	            stringParam('ansibleBranchName', "${jobPipelineVariables['cd'].ansibleBranchName[0]}", 'Please provide Application Branch Name')
				             	            stringParam('playBookName', "${jobPipelineVariables['cd'].playBookName[0]}", 'Please provide build command')
                                    		stringParam('inventoryFileName', "${jobPipelineVariables['cd'].inventoryFileName[0]}", 'Please provide build command')
                                    		stringParam('extras', "${jobPipelineVariables['cd'].extras[0]}", 'Please provide build command')
                                		}
                                	definition {
        				                cpsScm {
            					                scm {
                					                git {
                    						                remote {
                        						                credentials('9a922e65-7688-4308-8932-30a2672cb697')
                        						                github('Wu-Yongjian/jenkins-cicd-library', 'https',  'github.com')
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

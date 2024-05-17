#!groovy
library 'jian-cicd-library'
def call() {
	    node {	
                stage ("Generate init Floder and Jobs") {
                        jobDsl scriptText: """
				folder('jian-devops') {
    				   displayName('jian-devops')
    				   description('Master floder')
				}

				folder('jian-devops/cicd') {
    				   description('Folder containing all cicd jobs')
				}
				folder('jian-devops/ci') {
    				   description('Folder containing all ci jobs')
				}
				folder('jian-devops/cd') {
    				   description('Folder containing all cd jobs')
				}

                                pipelineJob('jian-devops/generateJobByParam') {
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
            			               scriptPath('libraries/vars/jian-generateJob-parameter.groovy')
                                                }
                                             }
    				   parameters {
        				stringParam {
            					name('projectName')
            					defaultValue('#please input your projectName #')
            					description('input your projectName')
                                                   }
        				stringParam {
            					name('jobType')
            					defaultValue('#please input your jobType #')
           				 	    description('input your jobType ci/cd/cicd')
                                                  }
					stringParam {
            					name('codeBuildType')
            					defaultValue('')
            					description('please input your codeBuildType if cd/cicd no need')
                                                        }
                                              }
                                   }
				   
				pipelineJob('jian-devops/generateJobByFileVars') {
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
            			               scriptPath('libraries/vars/jian-generateJob-fileVars.groovy')
                                                }
                                             }
    				   parameters {
        				stringParam {
            					name('projectName')
            					defaultValue('#please input your projectName #')
            					description('input your projectName')
                                                   }
        				stringParam {
            					name('jobType')
            					defaultValue('#please input your jobType #')
           				 	    description('input your jobType ci/cd/cicd')
                                                  }
        				stringParam {
            					name('varFileRepo')
            					defaultValue('#please input your varFileRepo #')
                                                        }
        				stringParam {
            					name('varFileRepoBranch')
            					defaultValue('#please input your varFileRepoBranch #')
                                                        }
        				stringParam {
            					name('varFileName')
            					defaultValue('#please input your varFileName #')
                                                        }
                                              }
                                   }
"""
	    }
}
}
call()	

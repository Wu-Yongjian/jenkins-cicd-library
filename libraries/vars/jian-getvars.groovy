#!groovy
def call(messagein){
	cfg = [:]
	cfg.put(projectName,messagein.projectName)
	cfg.put(jobType,message.jobType )
	cfg.put(buildType,message.buildType)
        return cfg

}

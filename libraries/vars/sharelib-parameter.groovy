#!groovy   
library 'jian-cicd-library'
node('slave'){
def cfg = [:]
// docker镜像build时的参数，格式为：'k1=v1 k2=v2 ...'，具体值请参考Dockerfile ARG
cfg.put('dockerBuildArg', 'JAR_NAME=target/*.jar')
cfg.put('envtype',"${envType}" )
cfg.put('buildtype',"${buildType}" )

//........其他配置...

// 启动Pipeline
pipelineStart(cfg)
}

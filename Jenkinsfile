#!/usr/bin/env groovy

pipeline {
  agent any

  options {
    ansiColor('xterm')
    timestamps()
  }

  libraries {
    lib("pay-jenkins-library@master")
  }

  environment {
    JAVA_HOME="/usr/lib/jvm/java-1.11.0-openjdk-amd64"
  }

  stages {
    stage('Maven Build') {
      steps {
        script {
          def long stepBuildTime = System.currentTimeMillis()
          if (env.BRANCH_NAME == 'master') {
            withCredentials([
                    string(credentialsId: 'bintray_username', variable: 'BINTRAY_USERNAME'),
                    string(credentialsId: 'bintray_api_key', variable: 'BINTRAY_APIKEY')]
            ) {
              def timestamp = new Date().format("yyyyMMddHHmmss", TimeZone.getTimeZone('UTC'))
              sh "mvn versions:set -DnewVersion=1.0.${timestamp}"
              sh 'mvn --settings settings.xml -Dbintray.username=${BINTRAY_USERNAME} -Dbintray.apiKey=${BINTRAY_APIKEY} deploy'
            }
          } else {
            sh 'mvn -version'
            sh 'mvn clean install'
          }
          postSuccessfulMetrics("pay-java-commons.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("pay-java-commons.maven-build.failure", 1)
        }
      }
    }
  }
  post {
    failure {
      postMetric(appendBranchSuffix("pay-java-commons") + ".failure", 1)
    }
    success {
      postSuccessfulMetrics(appendBranchSuffix("pay-java-commons"))
    }
  }
}

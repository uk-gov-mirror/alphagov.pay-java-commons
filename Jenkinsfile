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
  }

  stages {
    stage('Maven Build') {
      steps {
        script {
          def long stepBuildTime = System.currentTimeMillis()

          withCredentials([
                  string(credentialsId: 'bintray_username', variable: 'BINTRAY_USERNAME'),
                  string(credentialsId: 'bintray_apiKey', variable: 'BINTRAY_APIKEY')]
          ) {
            sh 'mvn versions:set -DnewVersion=1.0.0-${BUILD_NUMBER}'
            sh 'mvn --settings settings.xml -Dbintray.username=${BINTRAY_USERNAME} -Dbintray.apiKey=${BINTRAY_APIKEY} deploy '
          }

          postSuccessfulMetrics("pay-java-commons.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("ay-java-commons.maven-build.failure", 1)
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

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

          sh 'mvn clean package'

          postSuccessfulMetrics("pay-java-commons.maven-build", stepBuildTime)
        }
      }
      post {
        failure {
          postMetric("publicapi.maven-build.failure", 1)
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

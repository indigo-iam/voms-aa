#!/usr/bin/env groovy

@Library('sd')_
def kubeLabel = getKubeLabel()

pipeline {
  agent {
      kubernetes {
          label "${kubeLabel}"
          cloud 'Kube mwdevel'
          defaultContainer 'runner'
          inheritFrom 'ci-template'
      }
  }

  options {
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '5'))
    timeout(time: 1, unit: 'HOURS')
    timestamps()
  }

  triggers { cron('@daily') }

  environment {
    DOCKER_REGISTRY_HOST = "${env.DOCKER_REGISTRY_HOST}"
  }

  stages {
    stage('package'){
      steps {
        sh 'mvn -B package'
        sh 'mv target/*.jar docker/voms-aa.jar'
        archiveArtifacts 'docker/**'
      }
    }

    stage('docker-images'){
      steps {
        build job: 'kaniko-build-image/master', parameters: [string(name: 'UPSTREAM_PROJECT_NAME', value: "${currentBuild.fullProjectName}")]
      }
    }
  }

  post{
      failure {
        slackSend channel: '#iam', color: 'danger', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure (<${env.BUILD_URL}|Open>)"
      }
      
      changed {
        script{
          if('SUCCESS'.equals(currentBuild.currentResult)) {
            slackSend channel: '#iam', color: 'good', message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} Back to normal (<${env.BUILD_URL}|Open>)"
          }
        }
      }
    }

}

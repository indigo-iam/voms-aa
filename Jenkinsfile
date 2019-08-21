#!/usr/bin/env groovy

@Library('sd')_
def kubeLabel = getKubeLabel()

pipeline {
  agent any


  options {
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  environment {
    DOCKER_REGISTRY_HOST = "${env.DOCKER_REGISTRY_HOST}"
  }

  stages {
    stage('package'){

      agent {
          kubernetes {
              label "${kubeLabel}"
              cloud 'Kube mwdevel'
              defaultContainer 'runner'
              inheritFrom 'ci-template'
          }
      }

      steps {
        sh 'mvn -B -DskipTests package'
        stash includes: 'target/*.jar', name: 'jars'
      }
    }

    stage('docker-images'){
      agent {
          node {
            label "kaniko"
            customWorkspace "/workspace/jenkins/agent"
          }
      }

      steps {
        script {
            unstash 'jars'
            sh '''#!/busybox/sh
            cp target/*.jar docker/voms-aa.jar
            cd docker
            kaniko-build.sh
            '''
        }
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

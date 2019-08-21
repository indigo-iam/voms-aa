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
    timeout(time: 1, unit: 'HOURS')
    buildDiscarder(logRotator(numToKeepStr: '5'))
  }

  triggers { cron('@daily') }

  environment {
    DOCKER_REGISTRY_HOST = "${env.DOCKER_REGISTRY_HOST}"
  }

  stages {
    stage('package'){
      steps {
        sh 'mvn -B package'
        stash includes: 'docker/**', name: 'docker'
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
          container(name: 'runner', shell: '/busybox/sh') {
            unstash 'docker'
            unstash 'jars'
            sh '''#!/busybox/sh
            set -ex
            cp target/*.jar docker/voms-aa.jar
            cd docker
            kaniko-build.sh
            '''
          }
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

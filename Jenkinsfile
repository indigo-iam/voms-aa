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
        stash includes: 'docker/**', name: 'docker-image'
        stash includes: 'target/*.jar', name: 'jars'
      }
    }

    stage('docker-images'){
      steps {
        script {
          def dockerLabel = kubeLabel + '-docker'
          podTemplate(label: dockerLabel, cloud: 'Kube mwdevel', defaultContainer: 'runner', inheritFrom: 'kaniko-template'){
            node(dockerLabel) {
              container(name: 'runner', shell: '/busybox/sh') {
                unstash 'jars'
                sh '''#!/busybox/sh
                cp target/*.jar docker
                cd docker
                kaniko-build.sh
                '''
              }
            }
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

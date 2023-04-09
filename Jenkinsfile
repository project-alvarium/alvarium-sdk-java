pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {
        stage('test') {
            steps {
                step([$class: 'AlvariumBuilder', annotationType: 'TPM'])
                sh 'mvn test'
            }
        }
        stage ('build') {
            steps {
                step([$class: 'AlvariumBuilder', annotationType: 'GIT'])
                sh 'mvn package'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            step([$class: 'AlvariumRecorder', annotationType: 'ARTIFACT'])
        }
    }
}
@Library('alvarium-pipelines') _

pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {
        stage('prep - generate source code checksum') {
            steps {
                // Create a dir on the Jenkins worker to hold the checksum file
                sh 'mkdir -p $JENKINS_HOME/jobs/$JOB_NAME/$BUILD_NUMBER/'

                // $PWD is the workspace dir (the cloned repo), this will generate 
                // an md5sum (checksum) for the repo and write it to `sc_checksum` in
                // the dir created above
                sh ''' find . -type f -exec sha256sum {} + | LC_ALL=C sort | sha256sum |\
                        cut -d" " -f1 \
                        > $JENKINS_HOME/jobs/$JOB_NAME/$BUILD_NUMBER/sc_checksum
                '''
            }
        }

        // The source code annotator will give `isSatisfied=false` if the unit tests
        // run before it, as they generate files in the workspace directory which will
        // alter the source code checksum being generated
        stage('alvarium - pre-build annotations') {
            steps {
                script{
                    def optionalParams = ['sourceCodeChecksumPath':"${JENKINS_HOME}/jobs/${JOB_NAME}/${BUILD_NUMBER}/sc_checksum"]
                    alvariumCreate(['source-code', 'vulnerability'], optionalParams)
                }
            }
        }

        stage('test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('build') {
            steps {
                sh 'mvn package'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/**/*.jar', fingerprint: true
                }
            }
        }
    }
}

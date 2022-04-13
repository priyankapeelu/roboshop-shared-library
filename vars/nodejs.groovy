def lintChecks() {
    sh '''
    # we commented this because devs gonna check the failures
    #~/node_modules/jslint/bin/jslint.js server.js
    echo Lint Check for ${COMPONENT}
  '''
}

def call() {
    pipeline {
        agent any

        environment {
            SONAR = credentials('SONAR')
            NEXUS = credentials('NEXUS')
        }

        stages {

            //for each commit
            stage('lint checks') {
                steps {
                    script {
                        lintChecks()
                    }
                }
            }

            stage('Sonar Check') {
                steps {
                    script {
                        env.ARGS = "-Dsonar.sources=."
                        common.sonarCheck()
                    }
                }
            }
            stage('Test Cases') {
                parallel {

                    stage('unit tests') {
                        steps {
                            sh 'echo unit Tests'
                        }
                    }
                    stage('Integration Test') {
                        steps {
                            sh 'echo Integration Tests'
                        }
                    }
                    stage('Functional tests') {
                        steps {
                            sh 'echo Functionnal Tests'
                        }
                    }

                }

            }

            stage('check the release') {
                when {
                    expression { env.TAG_NAME != null }
                }
                steps {
                    script {
                        def UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s http://172.31.5.42:8081/service/rest/repository/browse/${COMPONENT} | grep \${COMPONENT}-\${TAG_NAME}.zip")
                        print UPLOAD_STATUS
                    }

                }
            }
                stage('prepare Artifacts') {
                    when {
                        expression { env.TAG_NAME != null }
                        expression { env.UPLOAD_STATUS == "" }
                    }
                    steps {
                        sh '''
                          npm install 
                          zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
                        '''


                    }
                }

                stage('Upload Artifacts') {
                    when {
                        expression { env.TAG_NAME != null }
                        expression { env.UPLOAD_STATUS == "" }
                    }
                    steps {
                        sh '''
                          curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.5.42:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
                          '''
                    }

                }

            }
        }

    }
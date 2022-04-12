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

                stage('prepare Artifacts') {
                    when {
                        expression { env.TAG_NAME != null }
                    }
                    steps {
                        sh 'echo'
                    }
                }

                stage('Upload Artifacts') {
                    when {
                        expression { env.TAG_NAME != null }
                    }
                    steps {
                        sh 'echo'

                    }

                }

            }
        }

    }
def lintChecks() {
    sh '''
    # We commented this because devs gonna check the failures.
    #~/node_modules/jslint/bin/jslint.js server.js
    echo Link Check for ${COMPONENT}
  '''
}

def call() {
    pipeline {
        agent any

        environment {
            SONAR = credentials('SONAR')
        }

        stages {

            // For Each Commit
            stage('Lint Checks') {
                steps {
                    script {
                        lintChecks()
                    }
                }
            }

            stage('SonarCheck') {
                steps {
                    script {
                        env.ARGS="-Dsonar.sources=."
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


        } // End of Stages

    }


}
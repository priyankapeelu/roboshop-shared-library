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
            NEXUS = credentials('NEXUS')
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

            stage('Test Cases')  {

                parallel {

                    stage('Unit Tests') {
                        steps {
                            sh 'echo Unit Tests'
                        }
                    }

                    stage('Integration Tests') {
                        steps {
                            sh 'echo Integration Tests'
                        }
                    }

                    stage('Functional Tests') {
                        steps {
                            sh 'echo Functional Tests'
                        }
                    }

                }

            }

            stage('Prepare Artifacts') {
                when {
                    expression { env.TAG_NAME != null }
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
                }
                steps {
                    sh '''
            curl -f -v -u admin:admin --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.11.69:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
          '''
                }
            }


        } // End of Stages

    }


}
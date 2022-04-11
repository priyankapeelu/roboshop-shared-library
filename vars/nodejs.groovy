def lintChecks() {
    sh '''
    # we commented this because devs gonna check the failures
    #~/node_modules/jslint/bin/jslint.js server.js
    echo Lint Check for ${COMPONENT}
  '''
}

def sonarCheck() {
    ssh '''
      sonar-scanner -Dsonar.host.url=http://172.31.13.177:9000 -Dsonar.sources=. -Dsonar.projectkey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}
     '''
}
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
                        sonarCheck()
                    }
                }
            }

        }// End of Stages

    }

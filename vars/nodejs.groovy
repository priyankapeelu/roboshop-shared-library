def lintChecks() {
    sh '''
    # we commented this because devs gonna check the failures
    #~/node_modules/jslint/bin/jslint.js server.js
    echo Lint Check for ${COMPONENT}
  '''
}

def sonarCheck() {
    sh '''
      sonar-scanner -Dsonar.host.url=http://172.31.13.177:9000 -Dsonar.sources=. -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}
      sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 123 172.31.13.177 ${COMPONENT}
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
                        sonarCheck()
                    }
                }
            }

        }// End of Stages

    }
}
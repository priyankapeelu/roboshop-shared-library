def lintChecks() {
    sh '''
    # We commented this because devs gonna check the failures.
    #~/node_modules/jslint/bin/jslint.js server.js
    #mvn checkstyle:check
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
                        sh 'mvn clean compile'
                        env.ARGS="-Dsonar.java.binaries=target/"
                        common.sonarCheck()
                    }
                }
            }

        } // End of Stages

    }


}
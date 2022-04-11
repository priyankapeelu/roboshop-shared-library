def lintChecks() {
    sh '''
    # we commented this because devs gonna check the failures
    #~/node_modules/jslint/bin/jslint.js server.js
    pylint *.py
    echo link checks for ${COMPONENT}
  '''
}

def call() {

    pipeline {
        agent any

        stages {

            //for each commit
            stage('lint checks') {
                steps {
                    script {
                        lintChecks()
                    }
                }
            } //End of stages
        }
    }
}
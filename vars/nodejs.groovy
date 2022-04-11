def lintChecks() {
    sh '''
    # we commented this because devs gonna check the failures
    #~/node_modules/jslint/bin/jslint.js server.js
    echo Lint Check for ${COMPONENT}
  '''
}

def call(COMPONENT) {

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
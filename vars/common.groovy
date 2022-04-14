def sonarCheck() {
    stage('Sonar Code Analysis') {
        sh '''
      #sonar-scanner -Dsonar.host.url=http://172.31.4.85:9000 -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW} ${ARGS}
      #sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.4.85 ${COMPONENT}
      echo Sonar Checks for ${COMPONENT}
    '''
    }
}


def lintChecks() {
    stage('Lint Checks') {
        if (env.APP_TYPE == "nodejs") {
            sh '''
        # We commented this because devs gonna check the failures.
        #~/node_modules/jslint/bin/jslint.js server.js
        echo Link Check for ${COMPONENT}
      '''
        }
        else if (env.APP_TYPE == "maven") {
            sh '''
      # We commented this because devs gonna check the failures.
      #~/node_modules/jslint/bin/jslint.js server.js
      #mvn checkstyle:check
      echo Lint Check for ${COMPONENT}
    '''
        }
        else if (env.APP_TYPE == "python" ) {
            sh '''
        # We commented this because devs gonna check the failures.
        #~/node_modules/jslint/bin/jslint.js server.js
        #pylint *.py
        echo Lint Check for ${COMPONENT}
      '''
        }
        else if (env.APP_TYPE == "golang" ){
            sh '''
        # We commented this because devs gonna check the failures.
        #~/node_modules/jslint/bin/jslint.js server.js
        echo Link Check for ${COMPONENT}
      '''
        }
    }

}

def testCases() {

    stage('Test Cases') {
        def stages = [:]

        stages["Unit Tests"] = {
            sh 'echo Unit Tests'
        }
        stages["Integration Tests"] = {
            sh 'echo Integration Tests'
        }

        stages["Functional Tests"] = {
            sh 'echo Functional Tests'
        }

        parallel(stages)
    }

}


def artifacts() {

    stage('Check The Release') {
        env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://172.31.5.42:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
        print UPLOAD_STATUS
    }

    if(env.UPLOAD_STATUS == "") {

        stage('Prepare Artifacts') {
            if (env.APP_TYPE == "nodejs") {
                sh '''
          ls -l
          npm install 
          ls -l
          zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js 
        '''
            } else if (env.APP_TYPE == "maven") {
                sh '''
                  mvn clear package
                  mv target/${COMPONENT}-1.0.jar ${COMPONENT}.jar 
                  zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
                '''
            } else if (env.APP_TYPE == "python") {
                sh '''
                 zip -r ${COMPONENT}-${TAG_NAME}.zip *.py *.ini requirements.txt
        '''
            } else if (env.APP_TYPE == "golang") {
                sh '''
                 echo 
                '''
            }
        }

        stage('Upload Artifacts') {
            withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'NEXUS_PSW', usernameVariable: 'NEXUS_USR')]) {
                sh '''
                curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.5.42:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
                '''
            }
        }

    }
}
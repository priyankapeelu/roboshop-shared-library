def sonarCheck() {
    sh '''
      sonar-scanner -Dsonar.host.url=http://172.31.13.177:9000 -Dsonar.sources=. -Dsonar.projectKey=${COMPONENT} -Dsonar.login=${SONAR_USR} -Dsonar.password=${SONAR_PSW}
      sonar-quality-gate.sh ${SONAR_USR} ${SONAR_PSW} 172.31.13.177 ${COMPONENT}
     '''
}
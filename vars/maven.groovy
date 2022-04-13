def call() {
    node {
        git branch: 'main', url: "https://github.com/raghudevopsb63/${COMPONENT}"
        env.APP_TYPE = "maven"
        common.lintChecks()
        sh 'mvn clean compile'
        env.ARGS="-Dsonar.java.binaries=target/"
        common.sonarCheck()
        common.testCases()

        if (env.TAG_NAME != null) {
            common.artifacts()
        }
    }
}
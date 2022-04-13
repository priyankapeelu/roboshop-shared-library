def call() {
    node {
        git branch: 'main', url: "https://github.com/raghudevopsb63/${COMPONENT}"
        env.APP_TYPE = "nodejs"
        common.lintChecks()
        env.ARGS="-Dsonar.sources=."
        common.sonarCheck()
        common.testCases()

        if (env.TAG_NAME != null) {
            common.artifacts()
        }
    }
}
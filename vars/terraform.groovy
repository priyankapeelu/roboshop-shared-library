def call() {

    properties([
            parameters([
                    choice(choices: 'dev\nprod', description: "Choose Environement", name: "ENV"),
            ]),
    ])

    node {
            sh 'rm -rf *'
            git branch: 'main', url: "https://github.com/raghudevopsb63/${REPONAME}"

            stage('Terrafile INIT') {
                sh 'terrafile -f env-${ENV}/Terrafile'
            }

    }
}


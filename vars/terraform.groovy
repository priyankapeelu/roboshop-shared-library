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


                stage('Terraform INIT') {
                    sh 'terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars'
                }

                stage('Terraform Plan') {
                    sh 'terraform plan -var-file=env-${ENV}/${ENV}.tfvars'
                }
            }

    }
}


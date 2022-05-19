def call() {

    if (!env.TERRAFORM_DIR) {
        env.TERRAFORM_DIR = "./"
    }

    properties([
            parameters([
                    choice(choices: 'dev\nprod', description: "Choose Environment", name: "ENV"),
                    string(choices: 'APP_VERSION', description: "APP VERSION", name: "APP_VERSION"),
            ]),
    ])

    node {
        ansiColor('xterm') {
            sh 'rm -rf *'
            git branch: 'main', url: "https://github.com/raghudevopsb63/${REPONAME}"

            stage('Terrafile INIT') {
                sh '''
          cd ${TERRAFORM_DIR}
          terrafile -f env-${ENV}/Terrafile
        '''
            }

            stage('Terraform INIT') {
                sh '''
          cd ${TERRAFORM_DIR}
          terraform init -backend-config=env-${ENV}/${ENV}-backend.tfvars
        '''
            }

            stage('Terraform Plan') {
                sh '''
          cd ${TERRAFORM_DIR}
          export TF_VAR_APP_VERSION=${APP_VERSION}
          terraform plan -var-file=env-${ENV}/${ENV}.tfvars 
        '''
            }

            stage('Terraform Apply') {
                sh '''
          cd ${TERRAFORM_DIR}
          export TF_VAR_APP_VERSION=${APP_VERSION}
          terraform apply -var-file=env-${ENV}/${ENV}.tfvars -auto-approve
        '''
            }

        }
    }
}
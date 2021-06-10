pipeline {
    agent {
        node {
            label 'maven'
        }

    }
    stages {

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests=true'
                archiveArtifacts(artifacts: 'target/*.jar', fingerprint: true)
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Check Project') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            echo "Using project: ${openshift.project()} in cluster ${openshift.cluster()}"
                        }
                    }
                }
            }
        }

        stage('Create Image Builder') {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            return !openshift.selector("bc", "${appName}-${env.BRANCH_NAME}").exists()
                        }
                    }
                }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            openshift.newBuild("--name=${appName}-${env.BRANCH_NAME}", "--image-stream=redhat-openjdk18-openshift:1.5", "--binary")
                        }
                    }
                }

            }
        }

        stage('Build Image') {
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            openshift.selector("bc", "${appName}-${env.BRANCH_NAME}").startBuild("--from-file=target/todo-list-jenkins-0.0.1-SNAPSHOT.jar", "--wait")
                        }
                    }
                }

            }
        }

        stage('Ask if promote to Prod') {
            when {
                beforeInput true
                branch 'production'
            }
            steps {
                office365ConnectorSend webhookUrl: "${office365WebhookUrl}",
                    message: "Para aplicar a mudança em produção, acesse [Janela de 10 minutos]: ${JOB_URL}",
                    status: "Alerta",
                    color: "#FFB818"

                timeout(time: 10, unit: 'MINUTES') {
                    input(id: "Deploy Gate", message: "Deploy em produção?", ok: 'Deploy')
                }
            }
        }

        stage("Create Env") {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            return !openshift.selector("dc", "${appName}-${env.BRANCH_NAME}").exists()
                        }
                    }
                }

            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject("${projectOpenshiftName}") {
                            openshift.newApp("${appName}:${env.BRANCH_NAME}", "--name=${appName}-${env.BRANCH_NAME}").narrow('svc').expose()
                        }
                    }
                }

            }
        }
    }

    post {
        success {
            office365ConnectorSend(
                    webhookUrl: "${office365WebhookUrl}",
                    message: "A Aplicação foi implantada em ambiente de ${env.BRANCH_NAME}" +
                            "<br>Duração total do pipeline: ${currentBuild.durationString}",
                    status: "Sucesso",
                    color: "#99C712"
            )
        }
        failure {
            office365ConnectorSend(
                    webhookUrl: "${office365WebhookUrl}",
                    message: "A Aplicação ${JOB_NAME} - ${BUILD_DISPLAY_NAME} sofreu uma falha durante o processo de build." +
                            "<br>Duração total do pipeline: ${currentBuild.durationString}",
                    status: "Falhou",
                    color: "#DC6650"
            )
        }
    }

    environment {
        appName = 'todolist'
        projectOpenshiftName = 'kalwitalo'
        office365WebhookUrl = 'https://techleadit.webhook.office.com/webhookb2/e9431669-990d-4cd1-95b5-095dd35512f3@c4ecbfec-df4a-4171-9e88-a56dff7d9839/JenkinsCI/f0725e283b9f4df19a45a1e42ef9f79a/d1568b67-4de9-4f2e-8fa4-a8ee1a59ef31'
    }
}

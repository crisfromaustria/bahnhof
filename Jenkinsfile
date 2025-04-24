pipeline { 
    agent any
    tools { 
	  git 'git'
      maven 'maven'
    }
    stages {
        stage('Validate') {
            steps {
                script {
                    sh 'git --version'
                    sh 'docker --version'
                }
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Maven Build'
                sh 'mvn -B -DskipTests clean package spring-boot:repackage'
            }
        }

        stage('Docker Build') {
            steps {
                script {
	            def dockerImage = docker.build("crisfromaustria/bahnhof")
		    docker.withRegistry('https://registry.hub.docker.com', '7f3fa0aa-0d56-44da-93e4-6892c559dd4e') {
			    dockerImage.push()
		    }
		}
            }
        }

        stage('Deploying Node App to Kubernetes') {
            steps {
                withCredentials([aws(accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'stephane', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    sh ('aws eks update-kubeconfig --name bahnhof-cluster --region us-east-1')
                    sh "kubectl get ns"
                    sh "kubectl apply -f bahnhof-wien.yaml"
                    sh "kubectl apply -f bahnhof-linz.yaml"
                    sh "kubectl apply -f bahnhof-salzburg.yaml"
                }
            }
        }
    }
}

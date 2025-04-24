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
	            sh 'sudo usermod -aG docker jenkins'
	            def dockerImage = docker.build("crisfromaustria/bahnhof")
		    docker.withRegistry('https://registry.hub.docker.com', '9cf7e916-2f92-45bd-896b-ff60e7373cd5') {
			    dockerImage.push()
		    }
		}
            }
        }

        stage('Deploying Node App to Kubernetes') {
            steps {
                sh ('aws eks update-kubeconfig --name bahnhof-cluster --region us-east-1')
                sh "kubectl get ns"
                sh "kubectl apply -f bahnhof-wien.yaml"
                sh "kubectl apply -f bahnhof-linz.yaml"
                sh "kubectl apply -f bahnhof-salzburg.yaml"
            }
        }
    }
}

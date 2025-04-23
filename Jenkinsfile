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
    }
}

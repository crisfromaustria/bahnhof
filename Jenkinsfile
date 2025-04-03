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
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Docker Build') {
            steps {
                script {
	            def dockerImage = docker.build("crisfromaustria/bahnhof")
		    docker.withRegistry('https://registry.hub.docker.com', '9cf7e916-2f92-45bd-896b-ff60e7373cd5') {
			    dockerImage.push()
		    }
		}
            }
        }
    }
}

pipeline {
    agent any
    tools { 
	  git 'git'
      maven 'maven'
    }
    stages {
        stage('Checkout') {
            steps {
                git scm
            }
        }

        stage('Validate') {
            steps {
                script {
                    sh 'ls -la'
                }
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Maven Build'
                sh 'mvn -B -DskipTests clean package'
            }
        }
    }
}

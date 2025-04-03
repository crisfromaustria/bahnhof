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
    }
}

pipeline {
    agent any
    tools { 
	  git 'git'
      maven 'maven'
    }
    stages {
        stage('Maven Build') {
            steps {
                echo 'Maven Build'
                sh 'mvn -B -DskipTests clean package'
            }
        }
    }
}

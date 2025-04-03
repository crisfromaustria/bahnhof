pipeline {
    agent any
    tools { 
      maven 'maven' 
    }
    stages {
        stage('Git') {
            steps {
                echo 'Git'
                git 'https://github.com/crisfromaustria/bahnhof.git'
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

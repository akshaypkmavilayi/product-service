pipeline{
    agent any
    tools{
        maven 'maven_build'
    }
    stages{
        stage('Build Maven'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/akshaypkmavilayi/product-service']])
                bat 'mvn clean install'
            }
        }
    }
}
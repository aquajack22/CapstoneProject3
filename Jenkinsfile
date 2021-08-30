pipeline {
  
    agent any
	
	  tools
    {
       maven "Maven"
    }
 stages {
      stage('checkout') {
           steps {
             
                git branch: 'main', url: 'https://github.com/aquajack22/CapstoneProject3.git'
             
          }
        }
	 stage('Execute Maven') {
           steps {
             
                sh 'mvn package'             
          }
        }
	stage('Test') {
      		steps {
        	sh 'mvn test'
      		}
    	}
        

  stage('Docker Build and Tag') {
           steps {
              
                sh 'docker build -t capstone3:latest .' 
               // sh 'docker tag capstone3 aquajack22/capstone3:latest'
                sh 'docker tag capstone3 aquajack22/capstone3:$BUILD_NUMBER'
               
          }
        }
     
  stage('Publish image to Docker Hub') {
          
            steps {
        withDockerRegistry([ credentialsId: "dockerHub", url: "" ]) {
        //  sh  'docker push aquajack22/capstone3:latest'
          sh  'docker push aquajack22/capstone3:$BUILD_NUMBER' 
        }
                  
          }
        }
     
      stage('Run Docker container on Jenkins Agent') {
             
            steps 
			{
                sh "docker run -d -p 8003:8080 aquajack22/capstone3"
 
            }
        }
 
    }
	}
    

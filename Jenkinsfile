pipeline {  
 agent any
 environment {		
        name = 'aquajack22/capstone3'
        tag = 'latest'       
        containerName = 'aetna'
	deploy = false
	registry = 'registry.hub.docker.com/aquajack22/capstone3'
    }
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
	  post{
                success {
                    script {
                        env.deploy = true
                    }
                }
                failure {
                    echo "========An execution error occured========"
                }
            }
    	}        

  	stage('Docker Build and Tag') {
	   when {
                expression {
                    return env.deploy
                }
            }
           steps {              
                sh 'docker build -t ${containerName}:${tag} .' 
                sh 'docker tag ${containerName} ${name}:${tag}'
              //sh 'docker tag ${containerName} ${name}:$BUILD_NUMBER'
               
          }
        }
     
  	stage('Publish image to Docker Hub') {
            when {
                expression {
                    return env.deploy
                }
            }
            steps {
		withDockerRegistry([ credentialsId: "dockerHub", url: "" ]) {
		  sh  'docker push ${name}:${tag}'
		  //sh  'docker push ${name}:$BUILD_NUMBER' 
        	}                  
          }
        }
     
	stage('Run Docker container on Jenkins Agent') {
	    when {
                expression {
                    return env.deploy
                }
            }
	    steps {
		sh "docker run -d -p 8003:8080 ${name}"
	     }
	} 
    }
   post {
        success {
            sh "docker rmi -f ${registry}:${tag}"
            //sh "docker rmi -f ${registry}:$BUILD_NUMBER"
        }
        failure {
            sh "docker rmi ${name}:${tag}"
        }
    }
}
    

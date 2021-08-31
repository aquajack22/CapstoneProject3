# CapstoneProject3
This is the Caltech DevOps Capstone project 3

B-Safe

DESCRIPTION
Create a CI/CD Pipeline to convert the legacy development process to a DevOps process.
Background of the problem statement:
A leading US healthcare company, Aetna, with a large IT structure had a 12-week release cycle and their business was impacted due to the legacy process. To gain
true business value through faster feature releases, better service quality, and cost optimization, they wanted to adopt agility in their build and release process.
The objective is to implement iterative deployments, continuous innovation, and automated testing through the assistance of the strategy.
Implementation requirements:
1.	Install and configure the Jenkins architecture on AWS instance
2.	Use the required plugins to run the build creation on a containerized platform
3.	Create and run the Docker image which will have the application artifacts
4.	Execute the automated tests on the created build
5.	Create your private repository and push the Docker image into the repository
6.	Expose the application on the respective ports so that the user can access the deployed application
7.	Remove container stack after completing the job
The following tools must be used:
1.	EC2
2.	Jenkins
3.	Docker
4.	Git
The following things to be kept in check:
1.	You need to document the steps and write the algorithms in them.
2.	The submission of your Github repository link is mandatory. In order to track your tasks, you need to share the link of the repository.
3.	Document the step-by-step process starting from creating test cases, the executing it, and recording the results.
4.	You need to submit the final specification document, which includes:
•	Project and tester details
•	Concepts used in the project
•	Links to the GitHub repository to verify the project completion
•	Your conclusion on enhancing the application and defining the USPs (Unique Selling Points)

Created by: Jagannath Rajagopalan
Tested by : Jagannath Rajagopalan
GitHub Repo: https://github.com/aquajack22/CapstoneProject3.git
Design

We will create a CI/CD process using Jenkins and Docker
This design is done with an intention of creating a CICD deployment for the application inside a container.
 
Implementation
Create an EC2 as Docker host

 
 
 
 
 
 
 
 



Setting up Jenkins Controller
1.	Create an EC2 instance as a Development environment to code and test the implementation
2.	This EC2 instance will be a Docker host in which we will deploy Jenkins as a container
3.	The following commands were run on root user to install docker on EC2 instance
sudo su -
yum update -y
yum install -y docker
service docker start
service docker status
 
4.	The following command were run to add ec2-user to docker
usermod -aG docker ec2-user
5.	Now we can test and run docker commands
6.	We exit to the  and try to run the following commands to check docker is accessible to ec2-user
docker ps
7.	If we get an access issue to the socket, then we run the following chmod command
chmod 666 /var/run/docker.sock
8.	We create a folder called Jenkins to keep our Jenkins related files
mkdir Jenkins
9.	Next we run the following set of commands to make Jenkins run as a container with volumes inside our docker host
a.	Create a bridge network in Docker 
docker network create jenkins
b.	In order to execute the docker commands inside jenkins node, download and run docker:dind image using the following command
docker run --name jenkins-docker --rm --detach   --privileged --network jenkins --network-alias docker   --env DOCKER_TLS_CERTDIR=/certs   --volume jenkins-docker-certs:/certs/client   --volume jenkins-data:/var/jenkins_home   --publish 2376:2376 docker:dind --storage-driver overlay2
10.	Next we customize the Jenkins image by using a Dockerfile 
a.	We use the following commands and save the Dockerfile inside 
cd jenkins
vim Dockerfile
FROM jenkins/jenkins
USER root
RUN apt-get update && apt-get install -y apt-transport-https \
       ca-certificates curl gnupg2 \
       software-properties-common
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN apt-key fingerprint 0EBFCD88
RUN add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/debian \
       $(lsb_release -cs) stable"
RUN apt-get update && apt-get install -y docker-ce-cli
USER jenkins
RUN jenkins-plugin-cli --plugins "blueocean:1.24.7 docker-workflow:1.26"
 
11.	Next we build the custom docker image of jenkins 
docker build -t myjenkins .
12.	The outcome is the creation of two images, one the original jenkins image, and our custom jenkins image which can be see by the command :

docker images
 
13.	Run the custom image of jenkins as a container in Docker using the following command
docker run --name jenkins-blueocean --rm --detach   --network jenkins --env DOCKER_HOST=tcp://docker:2376   --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1   --publish 8080:8080 --publish 50000:50000   --volume jenkins-data:/var/jenkins_home   --volume jenkins-docker-certs:/certs/client:ro   myjenkins
14.	We can see now both the docker:dind and myjenkins images running as containers using the command 
docker ps

 
15.	Now we can access the jenkins container using the command
docker exec -it jenkins-blueocean bash
 

16.	


Configuring Jenkins

1.	Check the public IP of the ec2 instance and use it to access the Jenkins
 
2.	Browse to http:// 18.217.218.52:8080 (or whichever port you configured for Jenkins when installing it) and wait until the Unlock Jenkins page appears 

3.	Provide the password, setup admin credentials and download the suggested plugins

 

4.	The Jenkins portal is ready for use 
 

5.	In the Global tool configuration section, update the Maven details to install it automatically.
Alternatively it can also be done through the console by installing inside the container.
 


6.	Please make sure that the Docker Pipeline plugin is installed since it will allow us to build and use Docker containers from pipelines
 

Create an EC2 as Agent

1.	Next we can create Jenkins build agents which can run the docker containers on the agent nodes. There can be several agents that can be created but for this exercise, we are creating an EC2 instance as an agent node.
2.	Since the jenkins controller container is a Debian instance, we will go with an ubuntu EC2 instance as the agent node.
 

 	
 
 
 
3.	In the security group, we must make sure that the custom tcp port of 8080 and 8003 are open to anywhere on the inbound side and the outbound is also open to anywhere
 
4.	Launch the instance and when its ready, it will look like this
 



Setting up Agent node
1.	The agent node will need Docker as it will allow the agent to run containers and be dynamic
2.	Install docker on the agent node with the following commands
sudo su –
apt update
apt upgrade
apt install docker.io
systemctl enable –now docker
usermod -aG docker ubuntu
3.	Install the same version of Java as present in the Jenkins controller with the following command
apt install default-jre
4.	Test the setup by checking the following commands
               docker –version
	service docker status

 
5.	Enable sudoers access to Ubuntu user 
 
6.	Enable password authentication on the agent node to ssh from the controller node by the following commands
vi /etc/ssh/sshd_config
systemctl restart ssh.service

 
7.	Set up password for the Ubuntu user to access the agent via SSH with following commands
passwd ubuntu
8.	Exit back to the ubuntu user from root user





Connecting Jenkins to Agent node

1.	In the jenkins console ,click on Build Executor Status or via Manage Jenkins, click on Manage nodes and clouds
2.	Click on new node and enter the details of the agent just created
 
3.	Keep the launch method as via SSH
4.	Enter the host name from the AWS console EC2 instance public IP 
 
5.	Use the Known host file verification strategy
6.	Click Save
7.	On the logs you should see the agent successfully connected and online 
 
8.	It would show both the controller and the agent node in Jenkins now
 

9.	Make the Number of executors on the controller or master node as 0 so that any job will always execute on the agent node which is as per the best practises
 

10.	In case you encounter issues while connecting the agent node to the master, please check if SSH is properly done between the controller and agent node
a.	Go to controller and share the keys with the agent with following commands
ssh-keygen -t rsa
ssh-copy-id ubuntu@3.139.239.80
b.	If all goes fine you should be able to connect to the agent from the controller with command

ssh ubuntu@3.139.239.80


11.	Now the agent is all set to receive any requests



Aetna Project

Its a spring boot project for an api created with spring initializer

 

Coding in VSCode
 



Checking if the spring boot project is running fine 
 
Created an api that returns a string on success

 
When accessing the api, we get the response locally. 

 

GitHub Repo

•	Create a GitHub project and check in the code of Aetna project. Also we will be checking in the Dockerfile and the Jenkinsfile that we will be using 

•	The link to the public GitHub repo is https://github.com/aquajack22/CapstoneProject3


 
•	The Dockerfile added to the project will use a prebuilt tomcat image. Over this image, it will add the war file that was built inside the container to the tomcat webapps directory so that microservice api can be accessed through any web browser or any api call.

 

•	The Jenkinsfile in the project will do the much of the work inside the container as follows
1.	Checkout from Git
2.	Execute maven and packages the WAR
3.	Runs test cases and presents a report
4.	If all tests are fine, builds the custom image using docker build and Dockerfile
5.	If all ok, pushes the image to the DockerHub repository
6.	Then if fine, runs the docker container using the image
7.	Post the container is ready, the old images are cleaned up.

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
	              //sh 'docker tag ${containerName} ${name}:${tag}'
	                sh 'docker tag ${containerName} ${name}:$BUILD_NUMBER'
	               
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
			  //sh  'docker push ${name}:${tag}'
			  sh  'docker push ${name}:$BUILD_NUMBER' 
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
	            //sh "docker rmi -f ${registry}:${tag}"
	            sh "docker rmi -f ${registry}:$BUILD_NUMBER"
	        }
	        failure {
	            sh "docker rmi ${name}:${tag}"
	        }
	    }
	}





Jenkins pipeline job

1.	Create a pipeline job called AetnaPipeline
 
 

2.	Under pipeline, select pipeline script from SCM and select GIT and provide the repo where the project lives. Specify the branch as main
 

3.	The script path should mention the exact file name path of the Jenkinsfile which is present in the github repo
 
4.	Save and run

Pre Requisite
1.	Before going forward with the execution, please make sure that the Docker Hub registry is available and is configured in Jenkins
 

2.	Configure the dockerHub variable used in the Jenkinsfile inside the jenkins 
 

Execution

a.	The present state of the images and containers on the agent docker node is as such :
 
b.	The jenkins shows the various steps performed successfully
 
c.	The logs show that the process was run on the agent node
 
d.	The agent node shows the images that were created
 

e.	The agent node also shows the container that is running the artifacts

 
f.	On accessing the docker hub repository, we could see the images getting put there
 

g.	On accessing the url via the browser, the api returns the message that the API call was a success
 

Conclusion

Advantages and USPs
•	Though we have been using jenkins pipeline for a while, the tooling that we have been using have been constrained to the agents. By having Docker available on all our agents, we can have full control of all the tools
•	Now we could use tools with specific versions in our pipeline so that they are not mandated by others
•	If someone else has to install all the tools on our agents, this can be bypassed as now we can dynamically bring in the tools that we need at runtime
•	Gives a chance to experiment with tools without having a long term commitment to those tools
•	The docker agents are ephemeral in nature which means they are only accessed when triggered.
•	With docker agents, we could have isolated execution environments. Eg one in Java 8 and other in Java 11
•	We can better utilise resources as we are using containerized solution and not entire VM

Extensions 
•	Currently the EC2 instances were created manually. This could be automated using Terraform or CloudFormation
•	Ansible could be installed on the Jenkins Controller node to automate installation of Docker on all agent nodes.
•	Agents could be scaled dynamically with the ports which Docker uses by using Docker Plugin as shown here and then using the Cloud feature with Docker and provide the host details with image info so that the instances could scale.
 

•	Another way to dynamically scale the agents is by using Kubernetes based Jenkins build agents.





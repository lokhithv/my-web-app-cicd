pipeline {
    agent any
    
    environment {
        AWS_REGION = 'ap-south-2'
        AWS_ACCOUNT_ID = '922754696039'
        ECR_REGISTRY = '922754696039.dkr.ecr.ap-south-2.amazonaws.com'
        ECR_REPOSITORY = 'my-web-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        AWS_CREDENTIALS = 'aws-credentials'
        CONTAINER_NAME = 'my-web-app-container'
        HOST_PORT = '8081'
        APP_URL = 'http://18.61.33.140:8081'
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '========================================='
                echo '=== Stage 1: Fetching code from GitHub ==='
                echo '========================================='
                checkout scm
            }
        }
        
        stage('Install Dependencies') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 2: Installing Node.js Dependencies ==='
                    echo '========================================='
                    sh 'npm install'
                }
            }
        }
        
        stage('Run Unit Tests') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 3: Running Unit Tests ==='
                    echo '========================================='
                    sh 'npm test'
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'coverage',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage Report'
                    ])
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 4: Building Docker Image ==='
                    echo '========================================='
                    sh "docker build -t ${ECR_REPOSITORY}:${IMAGE_TAG} ."
                    sh "docker tag ${ECR_REPOSITORY}:${IMAGE_TAG} ${ECR_REPOSITORY}:latest"
                    echo "Docker image built successfully: ${ECR_REPOSITORY}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Push to ECR') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 5: Pushing to AWS ECR ==='
                    echo '========================================='
                    withAWS(credentials: AWS_CREDENTIALS, region: AWS_REGION) {
                        sh """
                            echo "Logging into ECR..."
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                            
                            echo "Tagging images..."
                            docker tag ${ECR_REPOSITORY}:${IMAGE_TAG} ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                            docker tag ${ECR_REPOSITORY}:${IMAGE_TAG} ${ECR_REGISTRY}/${ECR_REPOSITORY}:latest
                            
                            echo "Pushing images to ECR..."
                            docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                            docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:latest
                            
                            echo "Images pushed successfully!"
                        """
                    }
                }
            }
        }
        
        stage('Deploy to EC2') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 6: Deploying to EC2 Instance ==='
                    echo '========================================='
                    withAWS(credentials: AWS_CREDENTIALS, region: AWS_REGION) {
                        sh """
                            echo "Logging into ECR for deployment..."
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
                            
                            echo "Stopping old container..."
                            docker stop ${CONTAINER_NAME} || true
                            docker rm ${CONTAINER_NAME} || true
                            
                            echo "Removing old images..."
                            docker rmi ${ECR_REGISTRY}/${ECR_REPOSITORY}:latest || true
                            
                            echo "Pulling latest image..."
                            docker pull ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                            
                            echo "Starting new container..."
                            docker run -d \
                                --name ${CONTAINER_NAME} \
                                -p ${HOST_PORT}:8080 \
                                --restart unless-stopped \
                                -e ENVIRONMENT=production \
                                -e PORT=8080 \
                                ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                            
                            echo "Waiting for application to start..."
                            sleep 15
                            
                            echo "Container started successfully!"
                            docker ps | grep ${CONTAINER_NAME}
                        """
                    }
                }
            }
        }
        
        stage('Verify Deployment') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 7: Verifying Deployment ==='
                    echo '========================================='
                    sh """
                        echo "Testing health endpoint..."
                        max_attempts=10
                        attempt=1
                        
                        while [ \$attempt -le \$max_attempts ]; do
                            echo "Attempt \$attempt of \$max_attempts..."
                            if curl -f http://localhost:${HOST_PORT}/health; then
                                echo "✅ Application is healthy and running!"
                                exit 0
                            fi
                            echo "Waiting for application..."
                            sleep 5
                            attempt=\$((attempt + 1))
                        done
                        
                        echo "❌ Health check failed"
                        exit 1
                    """
                }
            }
        }
        
        stage('Cleanup') {
            steps {
                script {
                    echo '========================================='
                    echo '=== Stage 8: Cleaning Up ==='
                    echo '========================================='
                    sh """
                        echo "Removing dangling images..."
                        docker image prune -f
                        echo "Cleanup complete!"
                    """
                }
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '=== ✅ PIPELINE COMPLETED SUCCESSFULLY! ==='
            echo '========================================='
            emailext (
                subject: "✅ SUCCESS: Node.js Pipeline - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                    <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2 style="color: #28a745;">✅ Node.js Pipeline Successful!</h2>
                        <hr>
                        <table style="border-collapse: collapse; width: 100%;">
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Project:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${env.JOB_NAME}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Build:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">#${env.BUILD_NUMBER}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;"><strong>Application URL:</strong></td>
                                <td style="padding: 8px; border: 1px solid #ddd;">
                                    <a href="${APP_URL}">${APP_URL}</a>
                                </td>
                            </tr>
                        </table>
                        <p><a href="${env.BUILD_URL}">View Build Details</a></p>
                    </body>
                    </html>
                """,
                to: 'lokhi123@gmail.com',
                mimeType: 'text/html'
            )
        }
        
        failure {
            emailext (
                subject: "❌ FAILURE: Node.js Pipeline - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check console: ${env.BUILD_URL}console",
                to: 'lokhi123@gmail.com'
            )
        }
        
        always {
            cleanWs()
        }
    }
}

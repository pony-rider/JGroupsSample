pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                withMaven(maven: "Maven 3.6.3", jdk: "jdk8") {
                    // Run the maven build
                    sh "mvn -B -e clean verify"
                }
            }
        }
    }
}

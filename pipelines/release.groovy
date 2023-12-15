library 'libs'
pipeline {
    agent none
    options {
        timestamps()
        ansiColor('xterm')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        lock(resource: "full-regression-suite")
    }
    environment {
        url           = getUrl()
        NEXUS_CREDS   = credentials('artifactory')
        TEST_HOST     = "10.36.118.11"
        TEST_USER     = "dent"
        RUN_NAME      = "${JOB_BASE_NAME}_${BUILD_NUMBER}"
    }
    stages {
        stage('Build & Publish'){
            agent {
                label "ubuntu-docker-aws-4c-16g"
            }
            stages {
                stage('Build') {
                    steps {
                        script {
                            buildDentOS()
                        }
                    }
                }
                stage('Publish to Nexus') {
                    steps {
                        script{
                            uploadToArtifactory()
                            getVersions()
                        }
                    }
                }
            }
        }
        stage('TriggerRegression') {
            agent {
                    label 'regression-test-trigger'
                }
            steps {
                script{
                    triggerRegression()
                }
            }
        }
    }
}

def getUrl(){
    if(BRANCH_NAME.contains('v')) {
        return "https://repos.refinery.dev/repository/dent/releases/org/dent/${BRANCH_NAME.replaceAll('v','')}/"
    }
    else {
        return "https://repos.refinery.dev/repository/dent/snapshots/org/dent/dentos/dentos-merge-main/"
    }
}
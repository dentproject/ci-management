library 'libs'
pipeline {
    agent none
    options {
        timestamps()
        ansiColor('xterm')
        buildDiscarder logRotator(artifactDaysToKeepStr: '185', artifactNumToKeepStr: '', daysToKeepStr: '185', numToKeepStr: '')
        disableConcurrentBuilds()
        lock(resource: "full-regression-suite")
    }
    triggers {
        cron('H 0 * * 5')
    }
    environment {
        url           = "https://repos.refinery.dev/repository/dent/snapshots/org/dent/dentos/dentos-merge-main-weekly/"
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
                            deleteDir()
                            git branch: 'main', credentialsId: 'dent-github-app', url: 'https://github.com/dentproject/dentOS.git'
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

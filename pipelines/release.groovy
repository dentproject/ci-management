pipeline {
    agent {
        label "ubuntu-docker-aws-4c-16g"
    }
    options {
        timeout(time: 3, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    environment {
        version       = "${TAG_NAME}".replaceAll("v","")
        url           = "https://repos.refinery.dev/repository/dent/releases/org/dent/${version}/"
        NEXUS_CREDS   = credentials('artifactory')
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
                uploadToArtifactory()
            }
        }
    }
}

def buildDentOS(){
    sh '''#!/bin/bash
        # be careful and verbose
        set -eux -o pipefail

        sudo apt-get update
        sudo apt-get install -y binfmt-support sysstat
        sudo systemctl start sysstat
        sudo systemctl enable sysstat
        make docker
    '''
}

def uploadToArtifactory() {

sh   '''#!/bin/bash
        upload_file() {
            local url="$1"
            local file_path="$2"
            local max_retries="$3"
            local retry_interval="$4"

            for ((i=1; i<=max_retries; i++)); do
                echo "Uploading file (attempt $i)..."
                response=$(curl -s -w '%{http_code}' -u "$NEXUS_CREDS" --upload-file "$file_path" "$url")
                if [[ "$response" == "2"* ]]; then
                    echo "File uploaded successfully."
                    return 0
                else
                    echo "File upload failed with HTTP status code $response."
                    if ((i < max_retries)); then
                        echo "Retrying in $retry_interval seconds..."
                        sleep "$retry_interval"
                    fi
                fi
            done

            echo "Maximum retries exceeded, file upload failed."
            return 1
        }

        # Set the pattern you want to match for files
        FILE_PATTERN="*DENTOS-HEAD*"
        directories=("${WORKSPACE}/builds/amd64/installer/installed/builds/stretch" "${WORKSPACE}/builds/arm64/installer/installed/builds/stretch" "${WORKSPACE}/builds/arm64/installer/installed/builds/buster" "${WORKSPACE}/builds/amd64/installer/installed/builds/buster")
        max_retries=3
        retry_interval=5

        # Loop through all directories provided as arguments
        for dir in "${directories[@]}"; do
                # Loop through all files in the current directory that match the pattern
                # shellcheck disable=SC2044
                for file in $(find "$dir" -type f -name "$FILE_PATTERN"); do
                    # Check if the file exists and is a regular file
                    if [ -e "$file" ]; then
                        echo "uploading: $file"
                        upload_file "$url$(basename "$file")" "$file" "$max_retries" "$retry_interval"
                    fi

                done
        done
        '''
}
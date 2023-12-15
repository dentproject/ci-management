def call(){
    sh '''#!/bin/bash
        # Exit on error, print each command, and exit if any command in a pipeline fails
        set -eux -o pipefail

        sudo apt-get update
        sudo apt-get install -y binfmt-support sysstat

        # Start and enable sysstat service
        sudo systemctl start sysstat
        sudo systemctl enable sysstat

        # Build dentos
        make docker
    '''
}
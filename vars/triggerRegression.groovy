def call(){
    sshagent(['dent-regression-ssh']) {
        // Execute tests supressing the error as command fails with non zero exit code
        sh """
            set +e
            ssh -o UserKnownHostsFile=/dev/null \
                -o StrictHostKeyChecking=no \
                ${TEST_USER}@${TEST_HOST} \"runDentCi -testSuite fullRegression -builds ${AMD_ARTIFACT_URL} ${ARM_ARTIFACT_URL} -testName jenkinsCI_${RUN_NAME}\"
            set -e
            """
        // Know the report path
        env.REPORT_PATH = sh(script: """
        ssh -o UserKnownHostsFile=/dev/null \
                -o StrictHostKeyChecking=no \
                ${TEST_USER}@${TEST_HOST} \"cat /home/dent/DentCiMgmt/CiResultPaths/ciResultPaths.json | jq -r --arg RUN_NAME $RUN_NAME '.results[] | select(.testName==\\"jenkinsCI_$RUN_NAME\\") | .resultPath'\"
        """,
        returnStdout: true).trim()
        print("Test Reports can be found in path ${REPORT_PATH}")
        // Download and archive the reports
        sh """
        scp -r -o UserKnownHostsFile=/dev/null \
                -o StrictHostKeyChecking=no \
                ${TEST_USER}@${TEST_HOST}:${REPORT_PATH} ${WORKSPACE}
        """
        archiveArtifacts artifacts: '**/*', followSymlinks: false
    }
}
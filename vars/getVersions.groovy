def call(){
    files = findFiles(glob: '**/**/*_INSTALLED_INSTALLER')
    files.each {
        file ->
        if(file.name.contains("ARM")){
            env.ARM_ARTIFACT_URL = "${url}${file.name}"
        }
        else if(file.name.contains("AMD")){
            env.AMD_ARTIFACT_URL = "${url}${file.name}"
        }
    }
    sh 'set +x; echo "ARM Artifact Name is ${ARM_ARTIFACT_URL}"'
    sh 'set +x; echo "AMD Artifact Name is ${AMD_ARTIFACT_URL}"'
}
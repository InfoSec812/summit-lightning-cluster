#!/usr/bin/groovy

node (''){
    env.APP_NAME = "${env.JOB_NAME}".replaceAll(/-?${env.PROJECT_NAME}-?/, '').replaceAll(/-?pipeline-?/, '')
    env.OCP_API_SERVER = "${env.OPENSHIFT_API_URL}"
    env.OCP_TOKEN = readFile('/var/run/secrets/kubernetes.io/serviceaccount/token').trim()

    // these should align to the projects in the Application Inventory
    def projectPrefix = "${env.PROJECT_NAME}".replaceAll(/-ci-cd/, '')
    env.DEV_PROJECT = "${projectPrefix}-dev"
    env.TEST_PROJECT = "${projectPrefix}-test"
    env.UAT_PROJECT = "${projectPrefix}-uat"

    env.SOURCE_CONTEXT_DIR = ""
    // this value is relative to env.SOURCE_CONTEXT_DIR
    env.UBER_JAR_CONTEXT_DIR = "target/"

    // this value should also include switches like -D and -P
    env.MVN_COMMAND = "clean package sonar:sonar deploy -Dhsql"

    // leave these alone unless you really know what you are doing
    env.MVN_SNAPSHOT_DEPLOYMENT_REPOSITORY = "nexus::default::http://nexus:8081/repository/labs-snapshots"
    env.MVN_RELEASE_DEPLOYMENT_REPOSITORY = "nexus::default::http://nexus:8081/repository/labs-releases"
}

node('mvn-build-pod') {

  stage('SCM Checkout') {
    checkout scm
  }

  dir ("${env.SOURCE_CONTEXT_DIR}") {
    stage('Build App') {
      withSonarQubeEnv('openshift-sonarqube') {
        // TODO - introduce a variable here
        if ( true ){
          sh "mvn ${env.MVN_COMMAND} -DaltDeploymentRepository=${MVN_SNAPSHOT_DEPLOYMENT_REPOSITORY}"
        } else {
          sh "mvn ${env.MVN_COMMAND} -DaltDeploymentRepository=${env.MVN_RELEASE_DEPLOYMENT_REPOSITORY}"
        }
      }
    }

    // assumes uber jar is created
    stage('Build Image') {
      sh "oc start-build ${env.APP_NAME} --from-dir=${env.UBER_JAR_CONTEXT_DIR} --follow"
    }
  }

  // no user changes should be needed below this point
  stage ('Deploy to Dev') {
    input "Promote Application to Dev?"

    openshiftTag (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", destStream: "${env.APP_NAME}", destTag: 'latest', destinationAuthToken: "${env.OCP_TOKEN}", destinationNamespace: "${env.DEV_PROJECT}", namespace: "${env.OPENSHIFT_BUILD_NAMESPACE}", srcStream: "${env.APP_NAME}", srcTag: 'latest')

    openshiftVerifyDeployment (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", depCfg: "${env.APP_NAME}", namespace: "${env.DEV_PROJECT}", verifyReplicaCount: true)
  }

  stage ('Deploy to Test') {
    input "Promote Application to Test?"

    openshiftTag (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", destStream: "${env.APP_NAME}", destTag: 'latest', destinationAuthToken: "${env.OCP_TOKEN}", destinationNamespace: "${env.TEST_PROJECT}", namespace: "${env.DEV_PROJECT}", srcStream: "${env.APP_NAME}", srcTag: 'latest')

    openshiftVerifyDeployment (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", depCfg: "${env.APP_NAME}", namespace: "${env.TEST_PROJECT}", verifyReplicaCount: true)
  }

  stage ('Deploy to UAT') {
    input "Promote Application to UAT?"

    openshiftTag (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", destStream: "${env.APP_NAME}", destTag: 'latest', destinationAuthToken: "${env.OCP_TOKEN}", destinationNamespace: "${env.UAT_PROJECT}", namespace: "${env.TEST_PROJECT}", srcStream: "${env.APP_NAME}", srcTag: 'latest')

    openshiftVerifyDeployment (apiURL: "${env.OCP_API_SERVER}", authToken: "${env.OCP_TOKEN}", depCfg: "${env.APP_NAME}", namespace: "${env.UAT_PROJECT}", verifyReplicaCount: true)
  }

}

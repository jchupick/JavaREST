## Summary

### Demo of fully working Jenkins pipeline executing the following steps:

  1. Pull JavaREST source code from a GitHub repo
  1. Build the Java code using Ant file and Jenkins built-in Ant integration
  1. Package built code into a deployable `.war` file
  1. Deploy the `.war` file to a TomCat instance (via a local share)
  1. Perform basic sanity check by running a curl command against the url cooresponding to the deployed code in Tomcat
  
### An Enterprise ready pipeline should also contain:

  1. (Following step 1. above) Perform static code analysis (eg SonarQube)
  1. (Following step 2. above) Create a versioned file for the current build. Associate that with the actual Jenkins job number.
  1. (Following step 3 above) Copy the built war file to an artifact repository (eg. Nexus)
  1. (Instead of step 4. above) Perform an actual deploy to as many servers as required, either in-network or in a cloud.
  1. (Instead of step 5. above) Perform testing (run-time, penetration, security) against built code using data pulled from a testing repository.

## JavaREST Used

### Source URL's
+ https://opensource.com/article/20/7/restful-services-java
+ https://condor.depaul.edu/mkalin/

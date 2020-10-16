## Summary

Demo of fully working Jenkins pipeline executing the following steps:

  1. Pull JavaREST source code from a GitHub repo
  1. Build the Java code using Ant file and Jenkins built-in Ant integration
  1. Package built code into a deployable .war file
  1. Deploy the .war file to a TomCat instance (via a local share)
  1. Perform basic sanity check by running a curl command against the url cooresponding to the deployed code in Tomcat

## JavaREST Used

### Source URL's
+ https://opensource.com/article/20/7/restful-services-java
+ https://condor.depaul.edu/mkalin/

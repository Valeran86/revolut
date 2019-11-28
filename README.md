###Assumptions and limitations of the prototype
1. Only one currency - `US dollar`
2. `Client` exist only as account's attribute
3. `InMemoryAccountRepository` - for simplifying example. Can be replaced on any other real DB realization.

### Build
`mvn clean package`

### Run server
`java -jar ./target/payments-api-1.0-SNAPSHOT-jar-with-dependencies.jar`

### Example of usages
`test curl scenario.sh`

#### Info & resources
1. https://dev.to/piczmar_0/framework-less-rest-api-in-java-1jbl
2. http://sparkjava.com/documentation
3. https://www.stubbornjava.com/posts/lightweight-embedded-java-rest-server-without-a-framework
4. https://www.eviltester.com/2018/04/overview-of-spark-and-testing.html
5. https://dzone.com/articles/testing-http-clients-using-the-spark-micro-framewo

###Task description
Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.    

####Explicit requirements:    
1. You can use Java or Kotlin.  
2. Keep it simple and to the point (e.g. no need to implement any authentication).  
3. Assume the API is invoked by multiple systems and services on behalf of end users.  
4. You can use frameworks/libraries if you like (​except Spring​), but don't forget about  requirement #2 and keep it simple and avoid heavy frameworks.  
5. The datastore should run in-memory for the sake of this test.  
6. The final result should be executable as a standalone program (should not require a  pre-installed container/server).  
7. Demonstrate with tests that the API works as expected.    

####Implicit requirements: 
1. The code produced by you is expected to be of high quality.  
2. There are no detailed requirements, use common sense. 
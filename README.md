## jsonCompare

A simple json compare tool written in Java Spring Boot.

### Requirements
  * `Java 1.8`  
  * `Maven 3.5 or later`
  
### Installation and Running
  * `git clone https://github.com/omerhanci/jsonCompare.git`
  * `cd jsonCompare`
  * `mvn package`
  * `java -jar jsonCompare-1.0.jar`
  
### Usage

  * `<host>/v1/diff/<ID>/left`  
  
  * `<host>/v1/diff/<ID>/right`  
  
  both accepts json data. You should post to these two endpoints the jsons that you want to compare, and then to see if they are different you should make a GET request to following endpoint, with the same id.

  * `<host>/v1/diff/<ID>`  
  
  You can use Postman to post data to following endpoints.

### Running tests

   * `mvn test`


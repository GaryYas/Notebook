# Notebook
 RESTful web service that can be used to save, retrieve and
update organized notes. The API should support the creation and deletion of “notebooks”. A notebook is
simply a collection of notes. The ability to retrieve a list of notebooks along with the number of notes in
each one should be possible

Implemented with following technologies: Spring boot, maven, Sprig data jpa(hibernate), H2 relational database, Swagger 3 open api ,junit 5, lombok, java 11 and
Caffeine cache.

For runnig you should have installed maven 3.65 and java 11. Or run through your preferred ide without maven installed.
For running without docker : java -jar knime-0.0.1-SNAPSHOT.jar under target folder or mvn spring-boot:run
Dockerfile provided for building docker image and running inside container.


For using the api and api documentation specification visit http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/ after running the web service.

For simplicity embeeded H2 in memmory data base was used. For configuring any other sql database just change the: spring.datasource.url=jdbc:h2:mem:testdb spring.datasource.driverClassName=org.h2.Driver in the application.properties file.


Example of using swagger rest api:
![image](https://user-images.githubusercontent.com/24994775/167726921-5ba1bb61-6c80-4e43-b5a9-bb750e0457d2.png)



for creating empty notebook use post request:
curl -X 'POST' \
  'http://localhost:8080/NoteBook?name=Gary' \
  -H 'accept: application/json' \
  -d ''

for creating note inside already created noteook use post request of note api:
curl -X 'POST' \
  'http://localhost:8080/NoteBook/Note' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "title": "string",
  "tags": [
    "string"
  ],
  "body": "string",
  "notebookId": 1
}

For all other crud request please follow open api instructions.


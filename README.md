mvn clean install
mvn package -P start-servers -pl run-app
mvn package -P stop-servers -pl run-app

localhost:9091 for front end

<user.hostname>localhost</user.hostname>
<user.http.port>5050</user.http.port>
<user.https.port>5051</user.https.port>

<frontend.http.port>9090</frontend.http.port>
<frontend.https.port>9091</frontend.https.port>
<frontend.hostname>localhost</frontend.hostname>

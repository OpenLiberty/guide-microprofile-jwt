mvn clean install
mvn package -P start-servers -pl run-app
mvn package -P stop-servers -pl run-app

localhost:9091 for front end

<backend.hostname>localhost</backend.hostname>
<backend.http.port>5050</backend.http.port>
<backend.https.port>5051</backend.https.port>

<frontend.http.port>9090</frontend.http.port>
<frontend.https.port>9091</frontend.https.port>
<frontend.hostname>localhost</frontend.hostname>

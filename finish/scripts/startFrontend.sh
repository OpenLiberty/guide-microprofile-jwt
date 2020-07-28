cd frontend
mvn liberty:create
mvn liberty:install-feature
mvn compile
mvn liberty:deploy
mvn liberty:start

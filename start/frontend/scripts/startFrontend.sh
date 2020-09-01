mvn liberty:create
mvn liberty:install-feature
mvn compile
mvn liberty:deploy
mvn liberty:start
# Add windows
# Investigate changing to bash
# Put into frontend, remove cd to avoid permissions issues
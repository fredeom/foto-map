Source: https://vaadin.com/wiki/-/wiki/Main/Using%20Vaadin%20with%20Maven

cd C:\Documents and Settings\papa\workspace\
mvn archetype:generate -DarchetypeGroupId=com.vaadin -DarchetypeArtifactId=vaadin-archetype-clean -DarchetypeVersion=LATEST
-DgroupId=meraschool -DartifactId=foto-map -Dversion=1.0 -Dpackaging=war
cd foto-map
mvn install > log.txt
mvn jetty:run

after adding of plugin:
mvn eclipse:eclipse
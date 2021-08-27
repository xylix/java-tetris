run:
	mvn -T 4 compile exec:java -Dexec.mainClass=kaantelypeli.ui.Main
build:
	mvn -T 4 package
test:
	mvn -T 4 test
integration-test:
	mvn -T 4 verify

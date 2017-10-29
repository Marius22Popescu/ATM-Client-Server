all: ATMServer.java AccountDB.java clean app
app:
	javac ATMServer.java
	ls -la
clean:
	rm -rf *.class
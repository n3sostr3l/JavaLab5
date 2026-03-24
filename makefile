JAR_DIR = target
CLIENT_JAR = $(JAR_DIR)/client.jar
SERVER_JAR = $(JAR_DIR)/server.jar
LOG_FILE = angel_logs/server.log

.PHONY: build server client clean start all
all: start
build:
	mvn clean package

start: server client

server:
	mkdir -p angel_logs
	-pkill -f server.jar
	nohup java -jar $(SERVER_JAR) > $(LOG_FILE) 2>&1 &

client:
	java -jar $(CLIENT_JAR)

clean:
	mvn clean

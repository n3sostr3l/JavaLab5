JAR_DIR = target
CLIENT_JAR = $(JAR_DIR)/client.jar
SERVER_JAR = $(JAR_DIR)/server.jar
ADMIN_JAR = $(JAR_DIR)/admin.jar
PORT = 12345
LOG_FILE = angel_logs/server.log

.PHONY: build server client admin clean start all logs

all: start

build:
	mvn clean package

server:
	mkdir -p angel_logs
	-pkill -f server.jar 2>/dev/null || true
	-fuser -k $(PORT)/tcp 2>/dev/null || true
	sleep 1
	nohup java -jar $(SERVER_JAR) > $(LOG_FILE) 2>&1 &
	@echo "Сервер запускается на порту $(PORT)..."

start: server
	@echo "Ожидание запуска сервера..."
	@sleep 2
	@$(MAKE) client

client:
	java -jar $(CLIENT_JAR)

admin:
	java -jar $(ADMIN_JAR)

logs:
	tail -f $(LOG_FILE)

clean:
	mvn clean

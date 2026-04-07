JAR_DIR = target
CLIENT_JAR = $(JAR_DIR)/client.jar
SERVER_JAR = $(JAR_DIR)/server.jar
ADMIN_JAR = $(JAR_DIR)/admin.jar
LOG_FILE = server.log
STUDENT_ID = s501858

.PHONY: build server client admin clean start all logs

all: remote

remote:
	@echo "Отправляем сервер на Helios..."
	scp -P 2222 $(SERVER_JAR) $(STUDENT_ID)@cs.ifmo.ru:./server.jar

	@echo "Останаливаем запущенный сервер"
	ssh -p 2222 $(STUDENT_ID)@cs.ifmo.ru "pkill -f server.jar 2>/dev/null || true"
	ssh -p 2222 $(STUDENT_ID)@cs.ifmo.ru "fuser -k 12345/tcp 2>/dev/null || true"
	sleep 4
	@echo "Запускаем сервер на Helios..."
	ssh -p 2222 $(STUDENT_ID)@cs.ifmo.ru "nohup java -Xms64m -Xmx128m -jar server.jar > server.log 2>&1 &"
	sleep 4

	@echo "Очищаю порт..."
	-fuser -k 12347/tcp 2>/dev/null || true

	@echo "Открываем порт 12347:12345..."
	ssh -f -N -L 12347:localhost:12345 $(STUDENT_ID)@cs.ifmo.ru -p 2222

	java -jar $(CLIENT_JAR)

build:
	mvn clean package

client:
	java -jar $(CLIENT_JAR)

admin:
	java -jar $(ADMIN_JAR)

logs:
	ssh -p 2222 s501858@cs.ifmo.ru "cat server.log"


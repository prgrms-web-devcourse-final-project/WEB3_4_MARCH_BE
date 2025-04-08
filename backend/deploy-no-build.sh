#!/bin/bash

# 변수 설정
JAR_NAME=backend-0.0.1-SNAPSHOT.jar
KEY_PATH=/c/Users/ho_go/team06-api-server-key.pem    # 👈 키 파일 정확한 위치 입력 (예: ~/Downloads/... )
REMOTE_USER=ubuntu
REMOTE_HOST=3.34.188.64
REMOTE_DIR=/home/ubuntu

echo "🚀 EC2로 JAR 파일 전송 중..."
scp -i $KEY_PATH build/libs/$JAR_NAME $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR/

echo "🧼 EC2에서 기존 프로세스 종료 중..."
ssh -i $KEY_PATH $REMOTE_USER@$REMOTE_HOST "pkill -f '$JAR_NAME' || true"

echo "🔁 EC2에서 JAR 실행 중..."
ssh -i $KEY_PATH $REMOTE_USER@$REMOTE_HOST "nohup java -jar $REMOTE_DIR/$JAR_NAME > app.log 2>&1 &"

echo "✅ 배포 완료 및 로그 출력 시작!"
ssh -i $KEY_PATH $REMOTE_USER@$REMOTE_HOST "tail -f $REMOTE_DIR/app.log"
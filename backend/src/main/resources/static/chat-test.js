// SockJS 연결할 때 포트 포함한 전체 주소로!
const socket = new SockJS("/ws");
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    console.log("WebSocket 연결 성공!");

    // 특정 채팅방 구독
    stompClient.subscribe("/api/sub/chat/123", function (message) {
      console.log("받은 메시지:", JSON.parse(message.body));
    });
});

// 메시지 보내기
function sendMessage() {
    const message = { roomId: "123", sender: "user1", content: "안녕하세요!" };
    stompClient.send("/api/pub/chat/send", {}, JSON.stringify(message));
}


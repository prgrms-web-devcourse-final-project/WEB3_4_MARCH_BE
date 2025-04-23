import { useEffect, useRef, useCallback } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useQueryClient } from "@tanstack/react-query";

export const useChatWebSocket = (roomId?: number) => {
  const clientRef = useRef<Client | null>(null);
  const queryClient = useQueryClient();

  // WebSocket 연결 설정
  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("/api/ws-endpoint"), // API 엔드포인트 경로 수정 필요
      connectHeaders: {
        // JWT 토큰을 헤더에 추가 (필요에 따라 수정)
      },
      debug: function(str) {
        console.log("STOMP: " + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (client.active) {
        client.deactivate();
      }
    };
  }, []);

  // 특정 채팅방 구독
  useEffect(() => {
    const client = clientRef.current;
    if (!client || !roomId || !client.active) return;

    const subscription = client.subscribe(`/topic/chat/rooms/${roomId}`, (message) => {
      try {
        const receivedMessage = JSON.parse(message.body);

        // 새 메시지가 도착하면 쿼리 캐시 갱신
        queryClient.invalidateQueries({ queryKey: ["chatMessages", roomId] });
        queryClient.invalidateQueries({ queryKey: ["chats"] });
      } catch (error) {
        console.error("메시지 파싱 실패:", error);
      }
    });

    return () => {
      subscription.unsubscribe();
    };
  }, [roomId, queryClient]);

  // 메시지 전송 함수
  const sendMessage = useCallback((content: string) => {
    const client = clientRef.current;
    if (!client || !roomId || !client.active) {
      console.error("WebSocket이 연결되지 않았습니다.");
      return false;
    }

    client.publish({
      destination: `/app/${roomId}/message`,
      body: JSON.stringify({ roomId, content }),
    });

    return true;
  }, [roomId]);

  return { sendMessage };
};
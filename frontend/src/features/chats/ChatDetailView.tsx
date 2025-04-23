import { useState, useRef, useEffect } from "react";
import { ArrowLeft, Send, Paperclip, Image, Smile } from "lucide-react";
import { cn } from "../../utils/classNaem";
import { useChatMessages, useSendMessage } from "../services/ChatMessageService";
import { useChatWebSocket } from "../hooks/useChatWebSocket";

// 사용자 정보 타입 정의
interface User {
  id: number;
  name: string;
  image: string | null;
}

interface ChatDetailProps {
  chatId: number;
  user: User;
  onBackClick: () => void;
}

export default function ChatDetailView({ chatId, user, onBackClick }: ChatDetailProps) {
  const [newMessage, setNewMessage] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);

  const { messages, isLoading, isError, fetchNextPage, hasNextPage } = useChatMessages(chatId);
  const { mutate: sendMessage } = useSendMessage(chatId);
  const { sendMessage: sendWebSocketMessage } = useChatWebSocket(chatId);

  // 메시지 목록이 업데이트될 때마다 스크롤을 아래로 이동
  useEffect(() => {
    if (!isLoading) {
      scrollToBottom();
    }
  }, [messages, isLoading]);

  // 스크롤 이벤트 처리
  const handleScroll = () => {
    const container = messagesContainerRef.current;
    if (container && container.scrollTop <= 100 && hasNextPage) {
      fetchNextPage();
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleSendMessage = () => {
    if (newMessage.trim() === "") return;

    // WebSocket으로 메시지 전송
    sendWebSocketMessage(newMessage.trim());

    // REST API로도 메시지 전송 (백업)
    sendMessage(newMessage.trim());

    // 메시지 입력창 초기화
    setNewMessage("");
  };

  return (
    <div className="h-full flex flex-col bg-gray-50">
      {/* Header */}
      <div className="px-4 py-3 bg-white border-b border-gray-200 flex items-center">
        <button
          onClick={onBackClick}
          className="mr-3 p-1 rounded-full hover:bg-gray-100"
        >
          <ArrowLeft className="h-5 w-5" />
        </button>

        <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden">
          {user.image ? (
            <div
              className="w-full h-full bg-center bg-cover"
              style={{ backgroundImage: `url(${user.image})` }}
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-gray-300 text-gray-600 font-medium">
              {user.name.charAt(0)}
            </div>
          )}
        </div>

        <div className="ml-3">
          <h2 className="text-sm font-medium">{user.name}</h2>
        </div>
      </div>

      {/* Messages */}
      <div
        ref={messagesContainerRef}
        className="flex-1 overflow-y-auto p-4 space-y-3"
        onScroll={handleScroll}
      >
        {isLoading ? (
          <div className="flex justify-center py-4">
            <p className="text-gray-500">메시지를 불러오는 중...</p>
          </div>
        ) : isError ? (
          <div className="flex justify-center py-4">
            <p className="text-red-500">메시지를 불러오는 데 실패했습니다.</p>
          </div>
        ) : messages.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full">
            <p className="text-gray-500">아직 메시지가 없습니다. 첫 메시지를 보내보세요!</p>
          </div>
        ) : (
          <>
            {hasNextPage && (
              <div className="flex justify-center">
                <button
                  onClick={() => fetchNextPage()}
                  className="text-sm text-gray-500 hover:underline"
                >
                  이전 메시지 더 보기
                </button>
              </div>
            )}

            {messages.map((message) => (
              <div
                key={message.id}
                className={cn(
                  "flex",
                  message.senderId === user.id ? "justify-start" : "justify-end"
                )}
              >
                <div
                  className={cn(
                    "max-w-[70%] rounded-2xl py-2 px-4",
                    message.senderId === user.id
                      ? "bg-white border border-gray-200 rounded-tl-none"
                      : "bg-black text-white rounded-tr-none"
                  )}
                >
                  <p className="text-sm">{message.content}</p>
                  <span className={cn(
                    "text-xs mt-1 block",
                    message.senderId === user.id ? "text-gray-500" : "text-gray-300 text-right"
                  )}>
                    {new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* Message Input */}
      <div className="p-3 bg-white border-t border-gray-200">
        <div className="flex items-center rounded-full bg-gray-100 px-4 py-2">
          <button className="text-gray-500 mr-2">
            <Paperclip className="h-5 w-5" />
          </button>
          <button className="text-gray-500 mr-2">
            <Image className="h-5 w-5" />
          </button>
          <input
            type="text"
            placeholder="메시지 입력..."
            className="flex-1 bg-transparent focus:outline-none text-sm"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleSendMessage();
              }
            }}
          />
          <button className="text-gray-500 ml-2">
            <Smile className="h-5 w-5" />
          </button>
          <button
            className={cn(
              "ml-2 p-1.5 rounded-full",
              newMessage.trim() !== ""
                ? "bg-black text-white"
                : "text-gray-400"
            )}
            onClick={handleSendMessage}
            disabled={newMessage.trim() === ""}
          >
            <Send className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
import { useState } from "react";
import ChatListView from "./ChatListView";
import ChatDetailView from "./ChatDetailView";
import { useChatRooms } from "./hooks/useChatRooms";
import { useChatWebSocket } from "./hooks/useChatWebSocket";

export default function ChatContainer() {
  const [selectedChatId, setSelectedChatId] = useState<number | null>(null);
  const { chatRooms, isLoading, isError, fetchNextPage, hasNextPage } = useChatRooms();

  // 모든 페이지의 채팅방을 하나의 배열로 평탄화
  const allChatRooms = chatRooms?.pages.flatMap(page => page.data?.content || []) || [];

  // WebSocket 연결
  useChatWebSocket(selectedChatId || undefined);

  const handleChatSelect = (chatId: number) => {
    setSelectedChatId(chatId);
  };

  const handleBackToList = () => {
    setSelectedChatId(null);
  };

  // 선택한 채팅방 정보 가져오기
  const selectedChat = selectedChatId
    ? allChatRooms.find(chat => chat.id === selectedChatId)
    : null;

  if (isLoading) {
    return (
      <div className="h-screen w-full max-w-md mx-auto flex items-center justify-center">
        <p>로딩 중...</p>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="h-screen w-full max-w-md mx-auto flex items-center justify-center">
        <p className="text-red-500">채팅방 목록을 불러오는 데 실패했습니다.</p>
      </div>
    );
  }

  return (
    <div className="h-screen w-full max-w-md mx-auto bg-white shadow-lg">
      {selectedChat ? (
        <ChatDetailView
          chatId={selectedChat.id}
          user={selectedChat.opponent}
          onBackClick={handleBackToList}
        />
      ) : (
        <ChatListView
          chatRooms={allChatRooms}
          onChatClick={handleChatSelect}
          onLoadMore={() => {
            if (hasNextPage) {
              fetchNextPage();
            }
          }}
          hasMore={!!hasNextPage}
        />
      )}
    </div>
  );
}
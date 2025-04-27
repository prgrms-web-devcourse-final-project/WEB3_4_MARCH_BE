import { useState } from "react";
import { Search } from "lucide-react";
import { cn } from "../../utils/classNaem";

interface User {
  id: number;
  name: string;
  image: string | null;
}

interface LastMessage {
  text: string;
  timestamp: string;
  isRead: boolean;
  isFromMe: boolean;
}

interface ChatRoom {
  id: number;
  opponent: User;
  lastMessage: LastMessage;
  unreadCount: number;
}

interface ChatListViewProps {
  chatRooms: ChatRoom[];
  onChatClick: (chatId: number) => void;
  onLoadMore: () => void;
  hasMore: boolean;
}

export default function ChatListView({ chatRooms, onChatClick, onLoadMore, hasMore }: ChatListViewProps) {
  const [searchQuery, setSearchQuery] = useState("");

  const filteredChats = searchQuery
    ? chatRooms.filter(
        (chat) =>
          chat.opponent.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
          chat.lastMessage.text.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : chatRooms;

  // 스크롤 이벤트 핸들러
  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const target = e.target as HTMLDivElement;
    // 스크롤이 바닥에 가까워지면 더 로드
    if (target.scrollHeight - target.scrollTop <= target.clientHeight + 100 && hasMore) {
      onLoadMore();
    }
  };

  return (
    <div className="h-full flex flex-col">
      {/* Search bar */}
      <div className="p-4 border-b border-gray-100">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <input
            type="text"
            placeholder="이름 또는 메시지 검색"
            className="w-full pl-10 pr-4 py-2 rounded-full bg-gray-100 text-sm focus:outline-none focus:ring-2 focus:ring-primary/20"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Chat list */}
      <div className="flex-1 overflow-auto" onScroll={handleScroll}>
        {filteredChats.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full p-4 text-center">
            <div className="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-4">
              <Search className="h-6 w-6 text-gray-400" />
            </div>
            <p className="text-gray-500">검색 결과가 없습니다</p>
          </div>
        ) : (
          <ul className="divide-y divide-gray-100">
            {filteredChats.map((chat) => (
              <li key={chat.id} className="hover:bg-gray-50">
                <button
                  className="w-full text-left py-3 px-4 flex items-start"
                  onClick={() => onChatClick(chat.id)}
                >
                  {/* User avatar */}
                  <div className="relative">
                    <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden">
                      {chat.opponent.image ? (
                        <div
                          className="w-full h-full bg-center bg-cover"
                          style={{ backgroundImage: `url(${chat.opponent.image})` }}
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center bg-gray-300 text-gray-600 font-medium text-lg">
                          {chat.opponent.name.charAt(0)}
                        </div>
                      )}
                    </div>
                    {chat.unreadCount > 0 && (
                      <div className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-black text-white text-xs flex items-center justify-center">
                        {chat.unreadCount}
                      </div>
                    )}
                  </div>

                  {/* Chat info */}
                  <div className="ml-3 flex-1 min-w-0">
                    <div className="flex justify-between items-baseline">
                      <h3 className="text-sm font-medium truncate">
                        {chat.opponent.name}
                      </h3>
                      <span className="text-xs text-gray-500 ml-2 whitespace-nowrap">
                        {chat.lastMessage.timestamp}
                      </span>
                    </div>
                    <p
                      className={cn(
                        "text-sm truncate mt-1",
                        chat.unreadCount > 0
                          ? "text-gray-900 font-medium"
                          : "text-gray-500"
                      )}
                    >
                      {chat.lastMessage.isFromMe && "나: "}
                      {chat.lastMessage.text}
                    </p>
                  </div>
                </button>
              </li>
            ))}
          </ul>
        )}

        {hasMore && (
          <div className="py-4 text-center">
            <p className="text-sm text-gray-500">더 불러오는 중...</p>
          </div>
        )}
      </div>
    </div>
  );
}
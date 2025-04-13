import ChatListView from "../features/chats/ChatListView";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useChatRooms } from "../features/chats/hooks/useChatRooms";
import { Loading } from "../components/Loading";

type ChatActivityProps = {
  params: {};
};

const ChatActivity: React.FC<ChatActivityProps> = ({ params: {} }) => {
  const { chatRooms, isLoading, isError } = useChatRooms();

  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
      {isLoading && (
        <div className="h-full w-full flex flex-col items-center justify-center">
          <Loading text="채팅방 목록을 불러오고 있습니다..." />
        </div>
      )}
      {isError && <div>채팅방 목록을 불러오는데 실패했습니다.</div>}
      {!isLoading && !isError && <ChatListView />}
    </AppScreenLayout>
  );
};

export default ChatActivity;

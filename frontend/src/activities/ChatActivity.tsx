import ChatListView from "../features/chats/ChatListView";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useChatRooms } from "../features/chats/hooks/useChatRooms";

type ChatActivityProps = {
  params: {};
};

const ChatActivity: React.FC<ChatActivityProps> = ({ params: {} }) => {
  const { chatRooms } = useChatRooms();

  console.log("###", chatRooms);

  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
      <ChatListView />
    </AppScreenLayout>
  );
};

export default ChatActivity;

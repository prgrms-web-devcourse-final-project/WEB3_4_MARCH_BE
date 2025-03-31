import ChatListView from "../features/chats/ChatListView";
import AppScreenLayout from "../layout/AppScreenLayout";

type ChatActivityProps = {
  params: {};
};

const ChatActivity: React.FC<ChatActivityProps> = ({ params: {} }) => {
  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
      <ChatListView />
    </AppScreenLayout>
  );
};

export default ChatActivity;

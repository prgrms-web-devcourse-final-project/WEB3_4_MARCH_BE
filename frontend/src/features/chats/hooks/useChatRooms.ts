import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../../api/apiClient";

type ChatRoom = {
  room_id: number;
  sender_id: number;
  receiver_id: number;
  profileImg: string | null;
  createdAt: string;
};

export const useChatRooms = () => {
  const { data: chatRooms } = useQuery<ChatRoom[]>({
    queryKey: ["chats"],
    queryFn: () => {
      console.log("hi");
      return apiClient.get("/api/chat/chatrooms");
    },
  });

  return { chatRooms };
};

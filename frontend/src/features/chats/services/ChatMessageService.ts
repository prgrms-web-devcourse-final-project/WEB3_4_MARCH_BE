// src/features/chats/services/ChatMessageService.ts

import { apiClient } from "../../../api/apiClient";
import { useMutation, useInfiniteQuery, useQueryClient } from "@tanstack/react-query";

// 메시지 관련 훅
export const useChatMessages = (roomId: number) => {
  const {
    data: messagesPages,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ["chatMessages", roomId],
    queryFn: ({ pageParam = 0 }) => {
      return apiClient.chatRoom.getChatMessages({
        roomId,
        page: pageParam,
        size: 20,
      });
    },
    getNextPageParam: (lastPage) => {
      if (lastPage.data?.last === true) {
        return undefined;
      }
      return (lastPage.data?.number ?? 0) + 1;
    },
    initialPageParam: 0,
  });

  // 모든 페이지의 메시지를 하나의 배열로 평탄화
  const messages = messagesPages?.pages.flatMap(page => page.data?.content || []) || [];

  return {
    messages,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  };
};

// 메시지 전송 훅
export const useSendMessage = (roomId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (content: string) => {
      return apiClient.chatRoom.sendMessage({
        roomId,
        content,
      });
    },
    onSuccess: () => {
      // 메시지 전송 성공 시 채팅 목록과 메시지 목록 갱신
      queryClient.invalidateQueries({ queryKey: ["chatMessages", roomId] });
      queryClient.invalidateQueries({ queryKey: ["chats"] });
    },
  });
};
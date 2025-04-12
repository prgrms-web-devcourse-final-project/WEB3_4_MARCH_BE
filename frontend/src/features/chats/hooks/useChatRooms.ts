import { useInfiniteQuery, useQuery } from "@tanstack/react-query";
import { apiClient } from "../../../api/apiClient";

export const useChatRooms = () => {
  const {
    data: chatRooms,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ["chats"],
    queryFn: ({ pageParam = 1 }) => {
      return apiClient.chatRoom.getChatRooms({
        page: pageParam,
        size: 10,
      });
    },
    getNextPageParam: (lastPage) => {
      // Check if this is the last page
      if (lastPage.data?.last === true) {
        return undefined;
      }
      // Return the next page number
      return (lastPage.data?.number ?? 0) + 1;
    },
    initialPageParam: 1,
  });

  return {
    chatRooms,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  };
};

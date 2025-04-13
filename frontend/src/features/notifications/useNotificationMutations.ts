import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiClient } from "../../api/apiClient";

export const useNotificationRead = () => {
  const queryClient = useQueryClient();

  const {
    mutate: markAsRead,
    isPending,
    isError,
  } = useMutation({
    mutationFn: ({
      memberId,
      notificationId,
    }: {
      memberId: number;
      notificationId: number;
    }) =>
      apiClient.notification.markAsRead({
        memberId,
        notificationId,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["notifications"],
      });
    },
  });

  return {
    markAsRead,
    isPending,
    isError,
  };
};

export const useNotificationDelete = () => {
  const queryClient = useQueryClient();

  const {
    mutate: deleteNotification,
    isPending,
    isError,
  } = useMutation({
    mutationFn: ({
      memberId,
      notificationId,
    }: { memberId: number; notificationId: number }) =>
      apiClient.notification.softDeleteNotification({
        memberId,
        notificationId,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["notifications"],
      });
    },
  });

  return {
    deleteNotification,
    isPending,
    isError,
  };
};

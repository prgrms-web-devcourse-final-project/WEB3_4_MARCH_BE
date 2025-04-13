import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../api/apiClient";

export const useNotifications = ({
  memberId,
}: {
  memberId?: number;
}) => {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => {
      if (!memberId) {
        return;
      }

      return apiClient.notification.getNotifications({
        memberId,
      });
    },
    enabled: !!memberId,
  });

  return {
    notifications: data?.data,
    isLoading,
    isError,
  };
};

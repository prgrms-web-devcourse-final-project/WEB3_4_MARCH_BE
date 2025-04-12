import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../api/apiClient";

export const useMatchings = () => {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["matchings"],
    queryFn: () => apiClient.userRecommend.getRecommendation(),
  });

  return { data, isLoading, isError };
};

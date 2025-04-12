import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../../api/apiClient";

const useKeywords = () => {
  const {
    data: keywords,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["keywords"],
    queryFn: () => apiClient.keyword.getAllKeywords(),
  });

  return {
    keywords,
    isLoading,
    isError,
  };
};

export default useKeywords;

import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../../api/apiClient";
import { keywordCategories } from "../dumy";

const useKeywords = () => {
  const {
    data: keywords,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["keywords"],
    queryFn: async () => {
      return keywordCategories;
      // return await apiClient.keyword.getAllKeywords();
    },
  });

  return {
    keywords,
    isLoading,
    isError,
  };
};

export default useKeywords;

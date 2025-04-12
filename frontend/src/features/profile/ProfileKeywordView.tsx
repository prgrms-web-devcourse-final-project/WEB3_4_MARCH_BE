import useKeywords from "../keywords/hooks/useKeywords";

export const ProfileKeywordView = () => {
  const { keywords, isLoading, isError } = useKeywords();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error...</div>;
  }

  return <div>ProfileKeywordView</div>;
};

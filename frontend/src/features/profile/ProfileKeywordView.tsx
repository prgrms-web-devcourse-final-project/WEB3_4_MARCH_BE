import { useState } from "react";
import useKeywords from "../keywords/hooks/useKeywords";
import { cn } from "../../utils/classNaem";
import { ArrowRight, Check } from "lucide-react";
import { Button } from "../../components/Button";
import { DUMMY_KEYWORDS } from "../keywords/dumy";

export const ProfileKeywordView = ({
  onComplete,
}: { onComplete: (keywords: number[]) => void }) => {
  const { keywords: keywordsData, isLoading, isError } = useKeywords();
  const keywords = keywordsData?.data ?? [];

  // const keywords = DUMMY_KEYWORDS;
  // const isLoading = false;
  // const isError = false;

  // State to track selected keyword IDs for each category
  const [selectedKeywords, setSelectedKeywords] = useState<
    Record<number, number[]>
  >(Object.fromEntries(keywords.map((category) => [category.categoryId, []])));

  // Toggle keyword selection
  const toggleKeyword = (categoryId: number, keywordId: number) => {
    const currentSelected = selectedKeywords[categoryId] || [];
    const category = keywords.find((c) => c.categoryId === categoryId);

    if (!category) return;

    if (currentSelected.includes(keywordId)) {
      // Remove keyword if already selected
      setSelectedKeywords({
        ...selectedKeywords,
        [categoryId]: currentSelected.filter((id) => id !== keywordId),
      });
    } else {
      if (category.multipleChoice) {
        // Add keyword to multiple selection
        setSelectedKeywords({
          ...selectedKeywords,
          [categoryId]: [...currentSelected, keywordId],
        });
      } else {
        // Replace selection for single choice categories
        setSelectedKeywords({
          ...selectedKeywords,
          [categoryId]: [keywordId],
        });
      }
    }
  };

  if (isLoading || !keywords) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error...</div>;
  }

  return (
    <div className="p-4 border-t border-gray-100 bg-white">
      {/* Content - Scrollable */}
      <div className="flex-1 overflow-auto">
        <div className="space-y-8 pb-4">
          {keywords.map((category) => (
            <div key={category.categoryId} className="space-y-3">
              <div className="mb-3">
                <h2 className="text-xl font-bold mb-1">
                  {category.categoryName}
                </h2>
              </div>

              <div className="flex flex-wrap gap-2">
                {category.keywords?.map((keyword) => {
                  if (!category.categoryId || !keyword.id) return null;

                  const isSelected = (
                    selectedKeywords[category.categoryId] || []
                  ).includes(keyword.id);

                  return (
                    <button
                      key={keyword.id}
                      onClick={() => {
                        if (!category.categoryId || !keyword.id) return;

                        toggleKeyword(category.categoryId, keyword.id);
                      }}
                      className={cn(
                        "px-4 py-2 rounded-full text-sm font-medium transition-all",
                        isSelected
                          ? "bg-black text-white"
                          : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50",
                      )}
                    >
                      {isSelected && (
                        <Check size={14} className="inline-block mr-1" />
                      )}
                      {keyword.name}
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>
      </div>

      <Button
        type="button"
        size="lg"
        fullWidth
        rightIcon={<ArrowRight size={18} />}
        onClick={() => {
          const keywordIds = Object.values(selectedKeywords).flat();
          onComplete(keywordIds);
        }}
      >
        완료
      </Button>
    </div>
  );
};

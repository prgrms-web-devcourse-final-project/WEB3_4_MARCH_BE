import { useState } from "react";
import useKeywords from "../keywords/hooks/useKeywords";
import { cn } from "../../utils/classNaem";
import { ArrowRight, Check } from "lucide-react";
import { Button } from "../../components/Button";

export const ProfileKeywordView = ({
  onComplete,
}: { onComplete: (keywords: string[]) => void }) => {
  const { keywords, isLoading, isError } = useKeywords();

  if (isLoading || !keywords) {
    return <div>Loading...</div>;
  }

  if (isError) {
    return <div>Error...</div>;
  }

  return (
    <div className="p-4 border-t border-gray-100 bg-white">
      <Button
        type="button"
        size="lg"
        fullWidth
        rightIcon={<ArrowRight size={18} />}
        onClick={() => onComplete([])}
      >
        완료
      </Button>
    </div>
  );
};

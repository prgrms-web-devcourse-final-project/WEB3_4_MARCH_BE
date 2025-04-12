import { Loader2 } from "lucide-react";

export const Loading = ({ text }: { text?: string }) => {
  return (
    <div className="flex flex-col items-center">
      <Loader2 className="h-12 w-12 text-primary animate-spin mb-4" />
      {text && <p className="text-gray-500 text-sm">{text}</p>}
    </div>
  );
};

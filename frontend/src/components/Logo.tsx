import { cn } from "../utils/classNaem";

export const Logo = ({
  showText = false,
  className = "",
}: {
  showText?: boolean;
  className?: string;
}) => (
  <div className={cn("flex items-center", className)}>
    <img src="/logo.svg" alt="Logo" className="h-8 w-8" />
    {showText && <span className="text-2xl font-bold ml-1">CONNECT TO</span>}
  </div>
);

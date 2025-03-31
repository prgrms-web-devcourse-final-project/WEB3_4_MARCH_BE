import { Bell } from "lucide-react";
import type { FC } from "react";

const TopBar: FC = () => {
  return (
    <div className="flex items-center justify-between h-full bg-white z-10">
      <img src="/logo.svg" alt="Logo" className="h-8 w-8" />

      <button className="p-2 text-gray-600 hover:text-primary transition-colors">
        <Bell size={22} />
      </button>
    </div>
  );
};

export default TopBar;

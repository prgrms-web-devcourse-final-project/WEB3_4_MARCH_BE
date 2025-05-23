import { useActivity, useFlow } from "@stackflow/react/future";
import { ArrowLeft, Bell } from "lucide-react";
import type { FC } from "react";
import { Logo } from "../components/Logo";

type TopBarProps = {
  backable?: boolean;
  title?: string;
};

const TopBar: FC<TopBarProps> = ({ backable, title }) => {
  const { pop, push } = useFlow();
  const { name } = useActivity();

  const handleBackClick = () => {
    pop();
  };

  const handleNotificationClick = () => {
    push("NotificationActivity", {});
  };

  return (
    <div className="flex items-center justify-between h-full bg-white z-10">
      {backable ? (
        <button
          onClick={handleBackClick}
          className="p-2 -ml-2 text-gray-600 hover:text-gray-900"
        >
          <ArrowLeft size={24} />
        </button>
      ) : (
        <Logo showText={false} />
      )}

      {title ? <div className="text-lg font-medium">{title}</div> : null}

      <button
        onClick={handleNotificationClick}
        className="p-2 text-gray-600 hover:text-primary transition-colors"
      >
        {name === "NotificationActivity" ? (
          <Bell size={22} fill="black" />
        ) : (
          <Bell size={22} />
        )}
      </button>
    </div>
  );
};

export default TopBar;

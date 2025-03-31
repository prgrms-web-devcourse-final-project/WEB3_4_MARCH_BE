import { Map as MapIcon, MessageSquare, Search, User } from "lucide-react";
import { useFlow } from "../stackflow/stackflow";
import { useActivity } from "@stackflow/react";

const BottomTabBar = () => {
  const { replace } = useFlow();
  const { name } = useActivity();

  return (
    <div className="flex items-center justify-around h-full bg-white z-10">
      <NavItem
        icon={<Search size={24} />}
        label="탐색"
        isActive={name === "ExploreActivity"}
        onClick={() =>
          replace(
            "ExploreActivity",
            {},
            {
              animate: false,
            },
          )
        }
      />
      <NavItem
        icon={<MapIcon size={24} />}
        label="지도"
        isActive={name === "MapActivity"}
        onClick={() =>
          replace(
            "MapActivity",
            {},
            {
              animate: false,
            },
          )
        }
      />
      <NavItem
        icon={<MessageSquare size={24} />}
        label="채팅"
        isActive={name === "ChatActivity"}
        onClick={() =>
          replace(
            "ChatActivity",
            {},
            {
              animate: false,
            },
          )
        }
      />
      <NavItem
        icon={<User size={24} />}
        label="내 정보"
        isActive={name === "ProfileActivity"}
        onClick={() =>
          replace(
            "ProfileActivity",
            {},
            {
              animate: false,
            },
          )
        }
      />
    </div>
  );
};

export default BottomTabBar;

function NavItem({
  icon,
  label,
  isActive,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  isActive: boolean;
  onClick: () => void;
}) {
  return (
    <button
      className="flex flex-col items-center justify-center w-full h-full"
      onClick={onClick}
    >
      <div className={`${isActive ? "text-primary" : "text-gray-400"}`}>
        {icon}
      </div>
      <span
        className={`text-xs mt-1 ${isActive ? "text-primary font-medium" : "text-gray-400"}`}
      >
        {label}
      </span>
    </button>
  );
}

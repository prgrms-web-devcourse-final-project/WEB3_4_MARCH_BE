import type { ReactNode, FC } from "react";
import { AppScreen } from "@stackflow/plugin-basic-ui";
import type { AppScreenProps } from "@stackflow/plugin-basic-ui";
import TopBar from "./TopBar";
import BottomTabBar from "./BottomTabBar";

interface AppScreenLayoutProps extends AppScreenProps {
  children: ReactNode;
  title?: string;
  noBottomBar?: boolean;
  backable?: boolean;
}

const AppScreenLayout: FC<AppScreenLayoutProps> = ({
  children,
  noBottomBar,
  backable,
  title,
  ...appScreenProps
}) => {
  return (
    <AppScreen {...appScreenProps} backgroundColor="beige">
      <div className="relative h-[100vh] w-full max-w-md mx-auto overflow-hidden flex flex-col bg-white">
        {/* Top Bar */}
        <div className="h-14 px-4 border-b border-gray-100">
          <TopBar backable={backable} title={title} />
        </div>

        {/* Content Area */}
        <div className="flex-1 overflow-auto px-4">{children}</div>

        {/* Bottom Navigation Bar */}
        {!noBottomBar && (
          <div className="h-16 border-t border-gray-100 ">
            <BottomTabBar />
          </div>
        )}
      </div>
    </AppScreen>
  );
};

export default AppScreenLayout;

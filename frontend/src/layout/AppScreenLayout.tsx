import type { ReactNode, FC } from "react";
import { AppScreen } from "@stackflow/plugin-basic-ui";
import type { AppScreenProps } from "@stackflow/plugin-basic-ui";
import TopBar from "./TopBar";
import BottomTabBar from "./BottomTabBar";
import { cn } from "../utils/classNaem";
import { LoginCheck } from "../features/auth/LoginCheck";
import { ErrorBoundary } from "react-error-boundary";

interface AppScreenLayoutProps extends AppScreenProps {
  children: ReactNode;
  title?: string;
  noTopBar?: boolean;
  noBottomBar?: boolean;
  backable?: boolean;
  wideScreen?: boolean;
  noLoginCheck?: boolean;
}

const AppScreenLayout: FC<AppScreenLayoutProps> = ({
  children,
  noTopBar,
  noBottomBar,
  backable,
  title,
  wideScreen,
  noLoginCheck = false,
  ...appScreenProps
}) => {
  return (
    <AppScreen {...appScreenProps}>
      <ErrorBoundary fallbackRender={() => <ErrorBoundaryFallback />}>
        <LoginCheck disabled={noLoginCheck}>
          <div className="relative h-[100vh] w-full max-w-md mx-auto overflow-hidden flex flex-col bg-white">
            {/* Top Bar */}
            {!noTopBar && (
              <div className="h-14 px-4 border-b border-gray-100">
                <TopBar backable={backable} title={title} />
              </div>
            )}

            {/* Content Area */}
            <div
              className={cn("flex-1 overflow-auto ", wideScreen ? "" : "px-4")}
            >
              {children}
            </div>
            {/* Bottom Navigation Bar */}
            {!noBottomBar && (
              <div className="h-16 border-t border-gray-100 ">
                <BottomTabBar />
              </div>
            )}
          </div>
        </LoginCheck>
      </ErrorBoundary>
    </AppScreen>
  );
};

export default AppScreenLayout;

const ErrorBoundaryFallback = () => {
  return (
    <div className="relative h-[100vh] w-full max-w-md mx-auto overflow-hidden flex flex-col bg-white">
      {/* Top Bar */}
      <div className="h-14 px-4 border-b border-gray-100">
        <TopBar />
      </div>

      {/* Content Area */}
      <div className={cn("flex-1 overflow-auto ", "px-4")}>
        애러가 발생했습니다.
      </div>
      {/* Bottom Navigation Bar */}
      <div className="h-16 border-t border-gray-100 ">
        <BottomTabBar />
      </div>
    </div>
  );
};

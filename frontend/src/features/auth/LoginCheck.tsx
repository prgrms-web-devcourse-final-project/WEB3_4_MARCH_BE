import { useActivity } from "@stackflow/react";
import { useFlow } from "../../stackflow/stackflow";
import { useEffect } from "react";
import { apiClient } from "../../api/apiClient";
import { useUserStore } from "./useUserStore";

export const LoginCheck = ({
  children,
  disabled,
  onAfterLoginCheck,
}: {
  disabled: boolean;
  children: React.ReactNode;
  onAfterLoginCheck?: (isLoggedIn: boolean) => void;
}) => {
  const { replace } = useFlow();
  const activeActivity = useActivity();

  const { setUserProfile } = useUserStore((s) => ({
    setUserProfile: s.setUserProfile,
  }));

  useEffect(() => {
    if (disabled) {
      return;
    }

    const checkLoginStatus = async () => {
      try {
        const response = await apiClient.member.getMyProfile();

        if (response.code === 200 && response.data) {
          setUserProfile(response.data);

          return {
            isLoggedIn: true,
            userProfile: response.data,
          };
        }

        return {
          isLoggedIn: false,
          userProfile: undefined,
        };
      } catch (error) {
        // 에러가 발생하면(특히 403) 로그인하지 않은 상태로 간주
        return {
          isLoggedIn: false,
          userProfile: undefined,
        };
      }
    };

    checkLoginStatus().then(({ isLoggedIn, userProfile }) => {
      onAfterLoginCheck?.(isLoggedIn);

      if (isLoggedIn) {
        console.log("login!", userProfile?.role);
        if (userProfile?.role === "ROLE_TEMP_USER") {
          replace("ProfileSetupActivity", {});
        }
        return;
      }

      if (activeActivity?.name === "LoginActivity") {
        return;
      }

      replace("LoginActivity", {}, { animate: false });
    });
  }, [replace, activeActivity, disabled, setUserProfile, onAfterLoginCheck]);

  return <div>{children}</div>;
};

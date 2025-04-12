import { useActivity } from "@stackflow/react";
import { useFlow } from "../../stackflow/stackflow";
import { useEffect } from "react";
import { apiClient } from "../../api/apiClient";

export const LoginCheck = ({
  children,
  disabled,
}: { disabled: boolean; children: React.ReactNode }) => {
  const { push } = useFlow();
  const activeActivity = useActivity();

  useEffect(() => {
    if (disabled) {
      return;
    }

    const checkLoginStatus = async () => {
      try {
        const response = await apiClient.member.getMyProfile();
        if (response.code === 200) {
          return true;
        }

        return false;
      } catch (error) {
        // 에러가 발생하면(특히 403) 로그인하지 않은 상태로 간주
        return false;
      }
    };

    checkLoginStatus().then((isLoggedIn) => {
      if (isLoggedIn) {
        return;
      }

      if (activeActivity?.name === "LoginActivity") {
        return;
      }

      push("LoginActivity", {});
    });
  }, [push, activeActivity, disabled]);

  return <div>{children}</div>;
};

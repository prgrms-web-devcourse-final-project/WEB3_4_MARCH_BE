import { useActivityParams } from "@stackflow/react";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useEffect } from "react";
import { apiClient } from "../api/apiClient";
import { useFlow } from "../stackflow/stackflow";
import { Loading } from "../components/Loading";

export const LoginLoadingActivity = () => {
  const { code } = useActivityParams<{ code?: string }>();

  const { replace } = useFlow();

  useEffect(() => {
    const handleLogin = async () => {
      try {
        const response = await apiClient.kakaoAuth.loginCallback({
          code,
        });

        if (response.code !== 200) {
          replace("LoginActivity", {});
          return;
        }

        if (response.data?.isRegistered) {
          replace("ExploreActivity", {});
        } else {
          replace("ProfileSetupActivity", {});
        }

        // biome-ignore lint/suspicious/noExplicitAny: API error handling requires any type
      } catch (error: any) {
        console.error("Login error details:", {
          message: error.message,
          status: error.response?.status,
          data: error.response?.data,
          code,
        });
      }
    };

    handleLogin();
  }, [code, replace]);

  return (
    <AppScreenLayout noLoginCheck noBottomBar>
      <div className="h-full w-full flex flex-col items-center justify-center">
        <Loading text="로그인 중입니다..." />
      </div>
    </AppScreenLayout>
  );
};

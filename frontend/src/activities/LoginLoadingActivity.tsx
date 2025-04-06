import { useActivityParams } from "@stackflow/react";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useEffect } from "react";
import { apiClient } from "../api/apiClient";
import { Loader2 } from "lucide-react";

export const LoginLoadingActivity = () => {
  const { code } = useActivityParams<{ code?: string }>();

  useEffect(() => {
    const handleLogin = async () => {
      if (code) {
        console.log("###code", code);
        const response = await apiClient.post("/api/auth/kakao/login", {
          code,
        });

        console.log("###response", response);
      }
    };

    handleLogin();
  }, [code]);

  return (
    <AppScreenLayout noLoginCheck noBottomBar>
      <div className="h-full w-full flex flex-col items-center justify-center">
        <div className="flex flex-col items-center">
          <Loader2 className="h-12 w-12 text-primary animate-spin mb-4" />
          <p className="text-gray-500 text-sm">잠시만 기다려주세요...</p>
        </div>
      </div>
    </AppScreenLayout>
  );
};

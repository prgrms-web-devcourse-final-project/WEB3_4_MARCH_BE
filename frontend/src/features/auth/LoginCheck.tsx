import { useActivity } from "@stackflow/react";
import { useFlow } from "../../stackflow/stackflow";
import { useEffect } from "react";
import authService from "./sessionStorageAuth";

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

    const isLoggedIn = authService.isLoggedIn();

    if (isLoggedIn) {
      return;
    }

    if (activeActivity?.name === "LoginActivity") {
      return;
    }

    push("LoginActivity", {});
  }, [push, activeActivity, disabled]);

  return <div>{children}</div>;
};

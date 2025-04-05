import AppScreenLayout from "../layout/AppScreenLayout";

import { useState } from "react";
import { ProfileKeywordView } from "../features/profile/ProfileKeywordView";
import {
  ProfileUserInfoSetupView,
  type UserInfo,
} from "../features/profile/ProfileUserInfoSetupView";
import { useFlow } from "../stackflow/stackflow";

export const ProfileSetupActivity = () => {
  const [page, setPage] = useState<"profile" | "keyword">("profile");
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

  const { push } = useFlow();

  const onConfirmProfileSetup = (userInfo: UserInfo) => {
    setUserInfo(userInfo);

    // setPage("keyword");
    push("ExploreActivity", {});
  };

  return (
    <AppScreenLayout noBottomBar noLoginCheck title="프로필 설정">
      {page === "profile" && (
        <ProfileUserInfoSetupView onComplete={onConfirmProfileSetup} />
      )}
      {page === "keyword" && <ProfileKeywordView />}
    </AppScreenLayout>
  );
};

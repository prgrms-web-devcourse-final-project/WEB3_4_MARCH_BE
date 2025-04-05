import AppScreenLayout from "../layout/AppScreenLayout";

import { useState } from "react";
import { ProfileKeywordView } from "../features/profile/ProfileKeywordView";
import {
  ProfileUserInfoSetupView,
  type UserInfo,
} from "../features/profile/ProfileUserInfoSetupView";

export const ProfileSetupActivity = () => {
  const [page, setPage] = useState<"profile" | "keyword">("profile");
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

  const onConfirmProfileSetup = (userInfo: UserInfo) => {
    setUserInfo(userInfo);

    setPage("keyword");
  };

  return (
    <AppScreenLayout noBottomBar title="프로필 설정">
      {page === "profile" && (
        <ProfileUserInfoSetupView onComplete={onConfirmProfileSetup} />
      )}
      {page === "keyword" && <ProfileKeywordView />}
    </AppScreenLayout>
  );
};

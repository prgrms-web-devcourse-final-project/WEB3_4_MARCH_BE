import AppScreenLayout from "../layout/AppScreenLayout";

import { useState } from "react";
import { ProfileKeywordView } from "../features/profile/ProfileKeywordView";
import {
  ProfileUserInfoSetupView,
  type UserInfo,
} from "../features/profile/ProfileUserInfoSetupView";
import { useFlow } from "../stackflow/stackflow";
import { useUserStore } from "../features/auth/useUserStore";
import type {
  MemberRegisterRequestDto,
  UserKeywordSaveRequest,
} from "../api/__generated__";
import { apiClient } from "../api/apiClient";

export const ProfileSetupActivity = () => {
  const [page, setPage] = useState<"profile" | "keyword">("profile");

  const [memberRegisterDto, setMemberRegisterDto] =
    useState<MemberRegisterRequestDto | null>(null);
  const [images, setImages] = useState<string[]>([]);
  const [keywords, setKeywords] = useState<UserKeywordSaveRequest | null>(null);

  const { push } = useFlow();
  const { profile } = useUserStore((s) => ({
    profile: s.profile,
  }));

  const onConfirmProfileSetup = (userInfo: UserInfo) => {
    const kakaoId = profile.id;

    if (!kakaoId) {
      return;
    }

    setMemberRegisterDto({
      age: Number(userInfo.age),
      email: userInfo.email,
      gender: userInfo.gender,
      height: Number(userInfo.height),
      nickname: userInfo.name,
      introduction: userInfo.bio,
      kakaoId,
    });

    setImages(userInfo.images);

    setPage("keyword");
  };

  const onConfirmKeywordSetup = (keywords: string[]) => {
    // apiClient.member.register({
    //   registerRequest: {
    //     member: {
    //       age: userInfo.age,
    //       email: userInfo.email,
    //       gender: userInfo.gender,
    //       height: userInfo.height,
    //       nickname: userInfo.name,
    //       kakaoId,
    //     },
    //     keywords,
    //   },
    // });

    push("ExploreActivity", {});
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

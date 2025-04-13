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
  RegisterOperationRequest,
  UserKeywordSaveRequest,
} from "../api/__generated__";
import { apiClient } from "../api/apiClient";
import {
  getCurrentPosition,
  isPermissionDeniedError,
  showBrowserPermissionGuide,
  showLocationPermissionDialog,
} from "../utils/currentPosition";

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

  const onConfirmProfileSetup = async (userInfo: UserInfo) => {
    const kakaoId = profile?.id;

    if (!kakaoId) {
      return;
    }

    const positionResult = await getCurrentPosition({
      retryPermission: true,
    });

    if (isPermissionDeniedError(positionResult)) {
      showBrowserPermissionGuide();
      return;
    }

    const position = positionResult as GeolocationPosition;

    setMemberRegisterDto({
      age: Number(userInfo.age),
      email: userInfo.email,
      gender: userInfo.gender,
      height: Number(userInfo.height),
      nickname: userInfo.name,
      introduction: userInfo.bio,
      kakaoId,
      latitude: position?.coords.latitude,
      longitude: position?.coords.longitude,
    });

    setImages(userInfo.images);

    setPage("keyword");
  };

  const onConfirmKeywordSetup = async (keywords: string[]) => {
    if (!memberRegisterDto) {
      return;
    }

    apiClient.member.register({
      registerRequest: {
        member: {
          age: memberRegisterDto.age,
          email: memberRegisterDto.email,
          gender: memberRegisterDto.gender,
          height: memberRegisterDto.height,
          nickname: memberRegisterDto.nickname,
          kakaoId: memberRegisterDto.kakaoId,
          latitude: memberRegisterDto.latitude,
          longitude: memberRegisterDto.longitude,
        },
        files: images,
        keywords: {
          keywordIds: [],
        },
      },
    });

    push("ExploreActivity", {});
  };

  return (
    <AppScreenLayout noBottomBar title="프로필 설정">
      {page === "profile" && (
        <ProfileUserInfoSetupView onComplete={onConfirmProfileSetup} />
      )}
      {page === "keyword" && (
        <ProfileKeywordView onComplete={onConfirmKeywordSetup} />
      )}
    </AppScreenLayout>
  );
};

import AppScreenLayout from "../layout/AppScreenLayout";

import { useState } from "react";
import { ProfileKeywordView } from "../features/profile/ProfileKeywordView";
import {
  ProfileUserInfoSetupView,
  type UserInfo,
} from "../features/profile/ProfileUserInfoSetupView";
import { useUserStore } from "../features/auth/useUserStore";
import type {
  MemberRegisterRequestDto,
  UserKeywordSaveRequest,
} from "../api/__generated__";
import { apiClient } from "../api/apiClient";
import {
  getCurrentPosition,
  isPermissionDeniedError,
  showBrowserPermissionGuide,
} from "../utils/currentPosition";
import { useFlow, type ActivityName } from "../stackflow/stackflow";

type ProfileSetupActivityParams = {
  redirectActivity?: ActivityName;
};

export const ProfileSetupActivity = ({
  params,
}: {
  params: ProfileSetupActivityParams;
}) => {
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

  const onConfirmKeywordSetup = async (keywordIds: number[]) => {
    if (!memberRegisterDto) {
      return;
    }

    apiClient.member.register({
      registerDTO: {
        member: {
          age: memberRegisterDto.age,
          email: memberRegisterDto.email,
          gender: memberRegisterDto.gender,
          height: memberRegisterDto.height,
          nickname: memberRegisterDto.nickname,
          kakaoId: memberRegisterDto.kakaoId,
          latitude: memberRegisterDto.latitude,
          longitude: memberRegisterDto.longitude,
          introduction: memberRegisterDto.introduction,
        },
        files: images,
        keywords: {
          keywordIds,
        },
      },
    });

    if (params.redirectActivity) {
      push(params.redirectActivity, {});
    } else {
      push("ExploreActivity", {});
    }
  };

  const imageUrls =
    (profile?.images?.map((image) => image.url)?.filter(Boolean) as string[]) ??
    [];

  return (
    <AppScreenLayout noBottomBar title="프로필 설정">
      {page === "profile" && (
        <ProfileUserInfoSetupView
          onComplete={onConfirmProfileSetup}
          defaultProfile={
            profile
              ? {
                  age: String(profile?.age) ?? "",
                  bio: profile?.introduction ?? "",
                  gender: profile?.gender as "male" | "female",
                  height: String(profile?.height) ?? "",
                  images: imageUrls,
                  name: profile?.nickname ?? "",
                  weight: "",
                  email: "",
                }
              : undefined
          }
        />
      )}
      {page === "keyword" && (
        <ProfileKeywordView onComplete={onConfirmKeywordSetup} />
      )}
    </AppScreenLayout>
  );
};

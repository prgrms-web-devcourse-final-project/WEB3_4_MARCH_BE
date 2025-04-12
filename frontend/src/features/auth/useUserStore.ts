import { create } from "zustand";
import { devtools } from "zustand/middleware";
import isEqual from "lodash/isEqual";
import type { MemberResponseDto } from "../../api/__generated__";

interface UserState {
  profile: MemberResponseDto;
  setUserProfile: (newProfile: MemberResponseDto) => void;
}

export const useUserStore = create<UserState>()(
  devtools(
    (set, get) => ({
      // 초기 상태
      profile: null,

      // 사용자 프로필 설정 액션
      setUserProfile: (newProfile: MemberResponseDto) => {
        const currentProfile = get().profile;

        // 데이터가 같으면 업데이트하지 않음
        if (isEqual(currentProfile, newProfile)) {
          return;
        }

        set({
          profile: newProfile,
        });
      },
    }),
    { name: "user-store" },
  ),
);

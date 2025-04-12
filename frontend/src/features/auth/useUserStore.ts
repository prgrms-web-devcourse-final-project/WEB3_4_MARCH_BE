import { create } from "zustand";
import isEqual from "lodash/isEqual";
import type { MemberResponseDto } from "../../api/__generated__";

interface UserState {
  profile: MemberResponseDto;
  setUserProfile: (newProfile: MemberResponseDto) => void;
}

export const useUserStore = create<UserState>((set, get) => ({
  // biome-ignore lint/suspicious/noExplicitAny: <explanation>
  profile: null as any,

  // 사용자 프로필 설정 액션
  setUserProfile: (newProfile: MemberResponseDto) => {
    const currentProfile = get().profile;

    // 현재 프로필이 null이거나 undefined일 경우에 대한 추가 검사
    if (!currentProfile || !newProfile) {
      set({ profile: newProfile });
      return;
    }

    // 데이터가 같으면 업데이트하지 않음
    if (isEqual(currentProfile, newProfile)) {
      return;
    }

    set({
      profile: newProfile,
    });
  },
}));

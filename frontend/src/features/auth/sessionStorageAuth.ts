/**
 * 사용자 인증 정보 관리를 위한 세션 스토리지 유틸리티
 *
 * 토큰, 사용자 정보를 안전하게 저장하고 관리합니다.
 * 브라우저 탭이 닫히면 모든 정보가 제거됩니다.
 */

// 타입 정의
export interface UserInfo {
  id: string;
  username: string;
  email: string;
  name?: string;
  profileImageUrl?: string;
  roles?: string[];
}

export interface AuthData {
  token: string;
  userInfo: UserInfo;
  expiresAt?: number; // 토큰 만료 시간 (밀리초 타임스탬프)
}

// 상수 정의
const AUTH_KEY = "auth_data";
const TOKEN_KEY = "auth_token";
const USER_INFO_KEY = "user_info";
const EXPIRES_AT_KEY = "token_expires_at";

/**
 * 세션 스토리지 유틸리티 클래스
 */
class SessionStorageAuth {
  /**
   * 사용자 로그인 처리 및 정보 저장
   */
  login(authData: AuthData): void {
    try {
      // 개별 항목 저장
      sessionStorage.setItem(TOKEN_KEY, authData.token);
      sessionStorage.setItem(USER_INFO_KEY, JSON.stringify(authData.userInfo));

      // 만료 시간이 있으면 저장
      if (authData.expiresAt) {
        sessionStorage.setItem(EXPIRES_AT_KEY, authData.expiresAt.toString());
      }

      // 전체 데이터도 저장 (선택적)
      sessionStorage.setItem(AUTH_KEY, JSON.stringify(authData));
    } catch (error) {
      console.error("로그인 정보 저장 실패:", error);
      this.clearAuthData();
      throw new Error("인증 정보를 저장할 수 없습니다.");
    }
  }

  /**
   * 로그아웃 - 모든 인증 데이터 삭제
   */
  logout(): void {
    this.clearAuthData();
  }

  /**
   * 모든 인증 데이터 삭제
   */
  private clearAuthData(): void {
    sessionStorage.removeItem(AUTH_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_INFO_KEY);
    sessionStorage.removeItem(EXPIRES_AT_KEY);
  }

  /**
   * 현재 저장된 인증 토큰 반환
   */
  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  /**
   * 사용자 정보 반환
   */
  getUserInfo(): UserInfo | null {
    const userInfoStr = sessionStorage.getItem(USER_INFO_KEY);
    if (!userInfoStr) return null;

    try {
      return JSON.parse(userInfoStr) as UserInfo;
    } catch (error) {
      console.error("사용자 정보 파싱 실패:", error);
      return null;
    }
  }

  /**
   * 전체 인증 데이터 반환
   */
  getAuthData(): AuthData | null {
    try {
      const token = this.getToken();
      const userInfo = this.getUserInfo();

      if (!token || !userInfo) return null;

      const expiresAtStr = sessionStorage.getItem(EXPIRES_AT_KEY);
      const expiresAt = expiresAtStr
        ? Number.parseInt(expiresAtStr, 10)
        : undefined;

      return { token, userInfo, expiresAt };
    } catch (error) {
      console.error("인증 데이터 조회 실패:", error);
      return null;
    }
  }

  /**
   * 사용자가 로그인되어 있는지 확인
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired();
  }

  /**
   * 토큰이 만료되었는지 확인
   */
  isTokenExpired(): boolean {
    const expiresAtStr = sessionStorage.getItem(EXPIRES_AT_KEY);
    if (!expiresAtStr) return false; // 만료 시간이 없으면 만료되지 않은 것으로 간주

    const expiresAt = Number.parseInt(expiresAtStr, 10);
    return Date.now() > expiresAt;
  }

  /**
   * 사용자 정보 업데이트
   */
  updateUserInfo(newUserInfo: Partial<UserInfo>): UserInfo | null {
    const currentUserInfo = this.getUserInfo();
    if (!currentUserInfo) return null;

    const updatedUserInfo = { ...currentUserInfo, ...newUserInfo };
    sessionStorage.setItem(USER_INFO_KEY, JSON.stringify(updatedUserInfo));

    // AUTH_KEY에도 업데이트 반영
    const authData = this.getAuthData();
    if (authData) {
      authData.userInfo = updatedUserInfo;
      sessionStorage.setItem(AUTH_KEY, JSON.stringify(authData));
    }

    return updatedUserInfo;
  }

  /**
   * 토큰 갱신
   */
  updateToken(newToken: string, expiresAt?: number): void {
    sessionStorage.setItem(TOKEN_KEY, newToken);

    if (expiresAt) {
      sessionStorage.setItem(EXPIRES_AT_KEY, expiresAt.toString());
    } else {
      sessionStorage.removeItem(EXPIRES_AT_KEY);
    }

    // AUTH_KEY에도 업데이트 반영
    const authData = this.getAuthData();
    if (authData) {
      authData.token = newToken;
      authData.expiresAt = expiresAt;
      sessionStorage.setItem(AUTH_KEY, JSON.stringify(authData));
    }
  }
}

// 싱글톤 인스턴스 생성 및 내보내기
const authService = new SessionStorageAuth();
export default authService;

import {
  BlockUserControllerApi,
  ChatControllerApi,
  ChatRequestControllerApi,
  ChatRoomControllerApi,
  Configuration,
  ImageControllerApi,
  KakaoAuthControllerApi,
  KeywordControllerApi,
  LikeControllerApi,
  MemberControllerApi,
  NotificationControllerApi,
  UserKeywordControllerApi,
  UserRecommendationControllerApi,
} from "./__generated__";

export const BACKEND_API = "https://api.connect-to.shop";

// export const BACKEND_API = "http://localhost:8080";

// API 클라이언트 설정
const config = new Configuration({
  basePath: BACKEND_API, // "http://localhost:8080",
  credentials: "include",
});

// 모든 API 클라이언트를 하나의 객체로 통합
export const apiClient = {
  block: new BlockUserControllerApi(config),
  chat: new ChatControllerApi(config),
  chatRequest: new ChatRequestControllerApi(config),
  chatRoom: new ChatRoomControllerApi(config),
  image: new ImageControllerApi(config),
  kakaoAuth: new KakaoAuthControllerApi(config),
  keyword: new KeywordControllerApi(config),
  like: new LikeControllerApi(config),
  member: new MemberControllerApi(config),
  notification: new NotificationControllerApi(config),
  userKeyword: new UserKeywordControllerApi(config),
  userRecommend: new UserRecommendationControllerApi(config),
};

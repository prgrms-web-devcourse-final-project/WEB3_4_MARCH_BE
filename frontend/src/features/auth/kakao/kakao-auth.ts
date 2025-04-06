export const getKakaoAuthUrl = ({
  clientId,
  redirectUri,
}: {
  clientId: string;
  redirectUri: string;
}) => {
  return `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code`;
};

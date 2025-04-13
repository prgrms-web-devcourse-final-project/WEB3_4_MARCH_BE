const ONE_MINUTE = 60;
const ONE_HOUR = 60 * ONE_MINUTE;
const ONE_DAY = 24 * ONE_HOUR;
const ONE_WEEK = 7 * ONE_DAY;
const ONE_MONTH = 30 * ONE_DAY;
const ONE_YEAR = 12 * ONE_MONTH;

export const formatDistanceToNow = (date: Date) => {
  const now = new Date();
  const past = date;
  const diffInSeconds = Math.floor((now.getTime() - past.getTime()) / 1000);

  if (diffInSeconds < 30) {
    return "1분 미만";
  } else if (diffInSeconds < ONE_MINUTE + 30) {
    return "1분";
  } else if (diffInSeconds < 44 * ONE_MINUTE + 30) {
    return `${Math.round(diffInSeconds / 60)}분`;
  } else if (diffInSeconds < 90 * ONE_MINUTE - 30) {
    return "약 1시간";
  } else if (diffInSeconds < 24 * ONE_HOUR - 30) {
    return `약 ${Math.round(diffInSeconds / ONE_HOUR)}시간`;
  } else if (diffInSeconds < 42 * ONE_HOUR - 30) {
    return "1일";
  } else if (diffInSeconds < 30 * ONE_DAY - 30) {
    return `${Math.round(diffInSeconds / ONE_DAY)}일`;
  } else if (diffInSeconds < 45 * ONE_DAY - 30) {
    return "약 1개월";
  } else if (diffInSeconds < 60 * ONE_DAY - 30) {
    return "약 2개월";
  } else if (diffInSeconds < 365 * ONE_DAY) {
    return `${Math.round(diffInSeconds / ONE_MONTH)}개월`;
  } else if (diffInSeconds < 365 * ONE_DAY + 90 * ONE_DAY) {
    return "약 1년";
  } else if (diffInSeconds < 365 * ONE_DAY + 270 * ONE_DAY) {
    return "1년 이상";
  } else if (diffInSeconds < 2 * 365 * ONE_DAY) {
    return "거의 2년";
  } else if (diffInSeconds < 3 * 365 * ONE_DAY) {
    return `약 ${Math.round(diffInSeconds / (365 * ONE_DAY))}년`;
  }
};

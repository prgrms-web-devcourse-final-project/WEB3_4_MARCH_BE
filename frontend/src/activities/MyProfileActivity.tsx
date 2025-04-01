import ProfileDetailView from "../features/profile/ProfileDetailView";
import AppScreenLayout from "../layout/AppScreenLayout";

type MyProfileActivityProps = {
  params: {};
};

// 내 프로필 더미 데이터
const myProfile = {
  id: 1,
  name: "김민준",
  age: 29,
  bio: "안녕하세요! 여행과 사진 찍는 것을 좋아하는 김민준입니다.\n새로운 친구들을 만나고 다양한 경험을 나누고 싶어요.\n관심있는 분야는 테크, 스타트업, 그리고 커피에요.",
  interests: ["여행", "사진", "테크", "커피", "영화", "독서"],
  image:
    "https://images.unsplash.com/photo-1600486913747-55e5470d6f40?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80",
  height: 182,
  location: "서울시 강남구",
  job: "프로덕트 매니저",
};

const MyProfileActivity: React.FC<MyProfileActivityProps> = ({
  params: {},
}) => {
  return (
    <AppScreenLayout title="CONNECT TO">
      <ProfileDetailView profile={myProfile} isMyProfile={true} />
    </AppScreenLayout>
  );
};

export default MyProfileActivity;

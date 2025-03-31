import ProfileDetailView from "../features/profile/ProfileDetailView";
import AppScreenLayout from "../layout/AppScreenLayout";

type ProfileDetailActivityProps = {
  params: {
    userId?: string;
  };
};

const ProfileDetailActivity: React.FC<ProfileDetailActivityProps> = ({
  params: { userId },
}) => {
  // Dummy profile data
  const dummyProfile = {
    id: 1,
    name: "김민지",
    age: 28,
    bio: "안녕하세요! 여행과 음식 탐방을 좋아하는 디자이너입니다. 새로운 경험을 즐기고 다양한 사람들과 소통하는 것을 좋아해요. 취미로 사진 찍는 것을 좋아합니다. 좋은 인연이 되었으면 좋겠어요!",
    interests: ["여행", "요리", "사진", "영화", "음악", "카페"],
    image:
      "https://images.unsplash.com/photo-1534528741775-53994a69daeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1064&q=80",
    height: 165,
    location: "서울시 강남구",
    job: "UX/UI 디자이너",
  };

  return (
    <AppScreenLayout noBottomBar backable title="김민지님의 프로필">
      <ProfileDetailView profile={dummyProfile} />
    </AppScreenLayout>
  );
};

export default ProfileDetailActivity;

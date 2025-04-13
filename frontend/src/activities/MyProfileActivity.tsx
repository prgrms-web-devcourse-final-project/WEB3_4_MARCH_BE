import { useUserStore } from "../features/auth/useUserStore";
import ProfileDetailView from "../features/profile/ProfileDetailView";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useFlow } from "../stackflow/stackflow";

type MyProfileActivityProps = {
  params: {};
};

const MyProfileActivity: React.FC<MyProfileActivityProps> = ({
  params: {},
}) => {
  const { push } = useFlow();

  const { myProfile } = useUserStore((s) => ({
    myProfile: s.profile,
  }));

  const handleProfileEditClick = () => {
    push("ProfileSetupActivity", {
      redirectActivity: "MyProfileActivity",
    });
  };

  return (
    <AppScreenLayout title="CONNECT TO">
      {myProfile && (
        <ProfileDetailView
          profile={{
            age: myProfile.age ?? 0,
            bio: myProfile.introduction ?? "",
            image: myProfile.images?.[0]?.url ?? "",
            interests:
              myProfile.keywords?.map((keyword) => keyword.name ?? "") ?? [],
            name: myProfile.nickname ?? "",
            height: myProfile.height ?? 0,
          }}
          isMyProfile={true}
          onProfileEditClick={handleProfileEditClick}
        />
      )}
    </AppScreenLayout>
  );
};

export default MyProfileActivity;

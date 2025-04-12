import { Heart, MessageSquare, Shield } from "lucide-react";
import { useState } from "react";
import { cn } from "../../utils/classNaem";

interface ProfileDetailProps {
  profile: {
    name: string;
    age: number;
    bio: string;
    interests: string[];
    image: string;
    height?: number;
    location?: string;
    job?: string;
  };
  isMyProfile?: boolean;
}

export default function ProfileDetailView({
  profile,
  isMyProfile,
}: ProfileDetailProps) {
  const [liked, setLiked] = useState(false);

  const handleLike = (e: React.MouseEvent) => {
    e.stopPropagation();
    setLiked(!liked);
  };

  const handleChatRequest = () => {
    // In a real app, this would send a chat request
    alert(`${profile.name}님에게 채팅 요청을 보냈습니다.`);
  };

  const handleBlock = () => {
    // In a real app, this would block the user
    alert(`${profile.name}님을 차단했습니다.`);
  };

  const handleEditProfile = () => {
    // In a real app, this would navigate to the edit profile screen
    alert("프로필 수정 화면으로 이동합니다.");
  };

  return (
    <div className="flex flex-col bg-white mb-20 pt-2">
      {/* Profile photo with like button */}
      <div className="relative">
        <div className="aspect-[4/3] w-full bg-gray-100">
          <div
            className="absolute inset-0 bg-center bg-cover rounded-md"
            style={{ backgroundImage: `url(${profile.image})` }}
          />
        </div>
        {!isMyProfile && (
          <button
            onClick={handleLike}
            className={cn(
              "absolute top-4 right-4 w-12 h-12 rounded-full flex items-center justify-center",
              liked
                ? "bg-red-500 text-white"
                : "bg-white/80 text-gray-600 backdrop-blur-sm",
            )}
          >
            <Heart size={24} className={liked ? "fill-white" : ""} />
          </button>
        )}
      </div>

      {/* Profile info */}
      <div className="py-4 space-y-6">
        {/* Name and basic info */}
        <div>
          <h1 className="text-2xl font-bold">
            {profile.name}, {profile.age}
          </h1>
          <div className="flex items-center mt-1 text-gray-500">
            {profile.job && <span className="mr-3">{profile.job}</span>}
            {profile.height && <span className="mr-3">{profile.height}cm</span>}
            {profile.location && <span>{profile.location}</span>}
          </div>
        </div>

        {/* Interests */}
        <div>
          <h3 className="text-lg font-medium mb-2">관심사</h3>
          <div className="flex flex-wrap gap-2">
            {profile.interests.map((interest) => (
              <span
                key={interest}
                className="px-3 py-1.5 bg-gray-100 rounded-full text-sm"
              >
                {interest}
              </span>
            ))}
          </div>
        </div>

        {/* Bio */}
        <div>
          <h3 className="text-lg font-medium mb-2">소개</h3>
          <p className="text-gray-700 whitespace-pre-line">{profile.bio}</p>
        </div>

        {/* Additional info - can be expanded in a real app */}
        <div>
          <h3 className="text-lg font-medium mb-2">기본 정보</h3>
          <div className="grid grid-cols-2 gap-y-2">
            <div className="text-gray-500">키</div>
            <div>{profile.height || 175}cm</div>
            <div className="text-gray-500">위치</div>
            <div>{profile.location || "서울시 강남구"}</div>
            <div className="text-gray-500">직업</div>
            <div>{profile.job || "디자이너"}</div>
          </div>
        </div>
      </div>

      {isMyProfile && (
        <div className="flex gap-3 mt-4">
          <button
            onClick={handleEditProfile}
            className="flex-1 py-3 bg-black text-white rounded-lg font-medium flex items-center justify-center"
          >
            프로필 수정
          </button>
        </div>
      )}

      {/* Action buttons */}
      {!isMyProfile && (
        <div className="fixed bottom-0 w-full h-20 left-0 p-4 border-t border-gray-100 bg-white">
          <div className="flex gap-3">
            <button
              onClick={handleChatRequest}
              className="flex-1 py-3 bg-black text-white rounded-lg font-medium flex items-center justify-center"
            >
              <MessageSquare size={18} className="mr-2" />
              채팅 요청하기
            </button>
            <button
              onClick={handleBlock}
              className="py-3 px-4 bg-gray-100 text-gray-700 rounded-lg font-medium flex items-center justify-center"
            >
              <Shield size={18} className="mr-2" />
              차단
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

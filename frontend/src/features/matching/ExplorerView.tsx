import { useEffect, useRef, useState } from "react";
import { Heart, RefreshCw, X, Search } from "lucide-react";
import { cn } from "../../utils/classNaem";
import { useFlow } from "../../stackflow/stackflow";
import type { RecommendedUserDto } from "../../api/__generated__";

type ProfileWithStatus = {
  profile: RecommendedUserDto;
  status: "active" | "swiping" | "removed";
  direction: "left" | "right" | null;
};

const TEMP_IMAGE = "/placeholder.svg?height=400&width=300";
const TEMP_KEYWORDS = ["여행", "독서", "카페", "디자인"];
const TEMP_AGE = 27;
const TEMP_BIO =
  "여행과 독서를 좋아하는 디자이너입니다. 같이 카페 투어 하실 분?";

export default function ExploreView({
  matchings,
}: {
  matchings: RecommendedUserDto[];
}) {
  const [profiles, setProfiles] = useState<ProfileWithStatus[]>(() =>
    matchings.map((profile, index) => ({
      profile,
      status: index === 0 ? "active" : "active",
      direction: null,
    })),
  );
  const [currentIndex, setCurrentIndex] = useState(0);
  const [startX, setStartX] = useState(0);
  const [offsetX, setOffsetX] = useState(0);
  const [isSwiping, setIsSwiping] = useState(false);
  const [showEndCard, setShowEndCard] = useState(false);
  const cardRef = useRef<HTMLDivElement>(null);

  const handleSwipeStart = (clientX: number) => {
    setStartX(clientX);
    setIsSwiping(true);
  };

  const handleSwipeMove = (clientX: number) => {
    if (!isSwiping) return;
    const newOffsetX = clientX - startX;
    setOffsetX(newOffsetX);
  };

  const handleSwipeEnd = () => {
    setIsSwiping(false);

    if (offsetX > 100) {
      handleLike();
    } else if (offsetX < -100) {
      handleDislike();
    } else {
      setOffsetX(0);
    }
  };

  const handleLike = () => {
    if (currentIndex < profiles.length) {
      // Update the current card to be swiping right
      setProfiles((prev) =>
        prev.map((item, idx) =>
          idx === currentIndex
            ? { ...item, status: "swiping", direction: "right" }
            : item,
        ),
      );

      // After animation, move to next card
      setTimeout(() => {
        setProfiles((prev) =>
          prev.map((item, idx) =>
            idx === currentIndex ? { ...item, status: "removed" } : item,
          ),
        );
        setCurrentIndex(currentIndex + 1);
        setOffsetX(0);
      }, 300);
    }
  };

  const handleDislike = () => {
    if (currentIndex < profiles.length) {
      // Update the current card to be swiping left
      setProfiles((prev) =>
        prev.map((item, idx) =>
          idx === currentIndex
            ? { ...item, status: "swiping", direction: "left" }
            : item,
        ),
      );

      // After animation, move to next card
      setTimeout(() => {
        setProfiles((prev) =>
          prev.map((item, idx) =>
            idx === currentIndex ? { ...item, status: "removed" } : item,
          ),
        );
        setCurrentIndex(currentIndex + 1);
        setOffsetX(0);
      }, 300);
    }
  };

  const handleRefresh = () => {
    setShowEndCard(false);
    setProfiles(
      matchings.map((profile) => ({
        profile,
        status: "active",
        direction: null,
      })),
    );
    setCurrentIndex(0);
  };

  // Check if we've reached the end of profiles
  useEffect(() => {
    if (currentIndex >= profiles.length) {
      setShowEndCard(true);
    }
  }, [currentIndex, profiles.length]);

  const { push } = useFlow();

  const handleProfileCardClick = () => {
    if (!profiles[currentIndex].profile.id) return;

    push("ProfileDetailActivity", {
      userId: profiles[currentIndex].profile.id.toString(),
    });
  };

  if (profiles.length === 0) {
    return (
      <div className="h-full flex flex-col items-center justify-center p-6 bg-gradient-to-br from-gray-50 to-gray-100">
        <div className="w-full max-w-md bg-white rounded-xl shadow-md overflow-hidden p-6 text-center animate-fade-in">
          <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center mx-auto mb-6">
            <Search size={32} className="text-primary" />
          </div>
          <h3 className="text-2xl font-bold text-gray-800 mb-3">
            추천 데이터가 없습니다
          </h3>
          <p className="text-gray-600 mb-6">
            아직 매칭할 수 있는 프로필이 준비되지 않았습니다.
            <br />
            조금 후에 다시 확인해주세요.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="h-full flex flex-col">
      <div className="relative flex-1 flex items-center justify-center overflow-hidden">
        {/* End of recommendations card */}
        {showEndCard && (
          <div className="w-full max-w-sm bg-white rounded-xl shadow-lg overflow-hidden z-20 animate-fade-in">
            <div className="aspect-[3/4] relative bg-gradient-to-br from-primary/10 to-primary/30 flex flex-col items-center justify-center p-6 text-center">
              <div className="w-20 h-20 rounded-full bg-black/10 flex items-center justify-center mb-6">
                <RefreshCw size={32} className="text-primary" />
              </div>
              <h3 className="text-2xl font-bold text-gray-800 mb-3">
                추천이 모두 끝났습니다
              </h3>
              <p className="text-gray-600 mb-8">
                지금까지 모든 추천 프로필을 확인하셨습니다.
                <br />
                하루뒤에 다시 확인해주세요.
              </p>
            </div>
          </div>
        )}

        {/* Stack of profile cards */}
        {!showEndCard && (
          <div className="relative w-full max-w-sm h-full flex items-center justify-center">
            {profiles.map((item, index) => {
              // Only render current and next few cards for performance
              if (index < currentIndex || index > currentIndex + 2) return null;

              const isActive = index === currentIndex;
              const isSwiping = item.status === "swiping";
              const isNext = index === currentIndex + 1;

              // Calculate transform based on status
              let transform = "";
              let opacity = 1;
              const zIndex = 10 - (index - currentIndex);

              if (isSwiping && item.direction === "right") {
                transform = "translateX(150%) rotate(30deg)";
              } else if (isSwiping && item.direction === "left") {
                transform = "translateX(-150%) rotate(-30deg)";
              } else if (isActive) {
                transform = `translateX(${offsetX}px) rotate(${offsetX * 0.05}deg)`;
              } else if (isNext) {
                transform = "scale(0.95)";
                opacity = 0.7;
              } else {
                transform = "scale(0.9)";
                opacity = 0.5;
              }

              const imageUrl = item.profile.images?.[0]?.url ?? TEMP_IMAGE;

              return (
                <div
                  onClick={handleProfileCardClick}
                  key={item.profile.id}
                  ref={isActive ? cardRef : null}
                  className={cn(
                    "absolute w-full max-w-sm bg-white rounded-xl shadow-lg overflow-hidden",
                    isSwiping
                      ? "transition-transform duration-300 ease-out"
                      : "",
                    !isSwiping && isActive && isSwiping
                      ? "transition-none"
                      : "",
                    !isSwiping && !isActive
                      ? "transition-all duration-300 ease-out pointer-events-none"
                      : "",
                  )}
                  style={{
                    transform,
                    opacity,
                    zIndex,
                  }}
                  onTouchStart={
                    isActive
                      ? (e) => handleSwipeStart(e.touches[0].clientX)
                      : undefined
                  }
                  onTouchMove={
                    isActive
                      ? (e) => handleSwipeMove(e.touches[0].clientX)
                      : undefined
                  }
                  onTouchEnd={isActive ? () => handleSwipeEnd() : undefined}
                  onMouseDown={
                    isActive ? (e) => handleSwipeStart(e.clientX) : undefined
                  }
                  onMouseMove={
                    isActive ? (e) => handleSwipeMove(e.clientX) : undefined
                  }
                  onMouseUp={isActive ? () => handleSwipeEnd() : undefined}
                  onMouseLeave={
                    isActive && isSwiping ? () => handleSwipeEnd() : undefined
                  }
                >
                  <div className="aspect-[5/7] relative bg-gray-100">
                    <div
                      className="absolute inset-0 bg-center bg-cover"
                      style={{ backgroundImage: `url(${imageUrl})` }}
                    />

                    {/* Profile info overlay */}
                    <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-4 text-white">
                      <div className="flex items-center">
                        <h3 className="text-xl font-bold">
                          {item.profile.nickname ?? "알 수 없음"}
                        </h3>
                        <span className="ml-2">{item.profile.age}</span>
                      </div>
                      <p className="mt-1 text-sm text-gray-200">
                        {item.profile.introduction ?? "알 수 없음"}
                      </p>
                      <div className="flex flex-wrap gap-1 mt-2">
                        {item.profile.keywords?.map((keyword) => (
                          <span
                            key={keyword.id}
                            className="px-2 py-1 bg-white/20 rounded-full text-xs backdrop-blur-sm"
                          >
                            {keyword.name}
                          </span>
                        ))}
                      </div>
                    </div>

                    {/* Swipe direction indicators */}
                    {isActive && offsetX > 50 && (
                      <div className="flex items-center justify-center h-full w-full">
                        <div className="flex items-center justify-center px-4 py-2 bg-green-500 text-white rounded-full font-bold">
                          좋아요
                        </div>
                      </div>
                    )}
                    {isActive && offsetX < -50 && (
                      <div className="flex items-center justify-center h-full w-full">
                        <div className="flex items-center justify-center px-4 py-2 bg-red-500 text-white rounded-full font-bold">
                          거절
                        </div>
                      </div>
                    )}

                    {/* Permanent indicators for swiping cards */}
                    {isSwiping && item.direction === "right" && (
                      <div className="flex items-center justify-center h-full w-full">
                        <div className="flex items-center justify-center px-4 py-2 bg-green-500 text-white rounded-full font-bold">
                          좋아요
                        </div>
                      </div>
                    )}
                    {isSwiping && item.direction === "left" && (
                      <div className="flex items-center justify-center h-full w-full">
                        <div className="flex items-center justify-center px-4 py-2 bg-red-500 text-white rounded-full font-bold">
                          거절
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Action buttons - only show when there are profiles to swipe */}
      {!showEndCard && (
        <div className="flex justify-center gap-4 mb-4">
          <button
            onClick={handleDislike}
            className="w-14 h-14 flex items-center justify-center rounded-full bg-white border border-gray-200 shadow-sm text-red-500 hover:bg-red-50 transition-colors"
          >
            <X size={24} />
          </button>
          <button
            onClick={handleLike}
            className="w-14 h-14 flex items-center justify-center rounded-full bg-white border border-gray-200 shadow-sm text-green-500 hover:bg-green-50 transition-colors"
          >
            <Heart size={24} />
          </button>
        </div>
      )}
    </div>
  );
}

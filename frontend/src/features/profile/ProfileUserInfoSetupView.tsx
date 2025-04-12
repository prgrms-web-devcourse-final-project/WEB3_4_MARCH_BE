import { Camera, X } from "lucide-react";
import { cn } from "../../utils/classNaem";
import { useState } from "react";
import { Button } from "../../components/Button";

export interface UserInfo {
  images: string[];
  name: string;
  bio: string;
  gender: "male" | "female";
  age: number;
  height: number;
  weight: number;
  email: string;
}

export const ProfileUserInfoSetupView = ({
  onComplete,
}: {
  onComplete: (userInfo: UserInfo) => void;
}) => {
  const [images, setImages] = useState<UserInfo["images"]>([]);
  const [name, setName] = useState<UserInfo["name"]>("");
  const [bio, setBio] = useState<UserInfo["bio"]>("");
  const [gender, setGender] = useState<UserInfo["gender"] | null>(null);
  const [age, setAge] = useState<UserInfo["age"]>(0);
  const [height, setHeight] = useState<UserInfo["height"]>(0);
  const [weight, setWeight] = useState<UserInfo["weight"]>(0);
  const [email, setEmail] = useState<UserInfo["email"]>(""); // 이메일 상태 추가
  const [errors, setErrors] = useState<Record<string, string>>({});

  const maxImages = 5;

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      if (images.length >= maxImages) {
        alert(`최대 ${maxImages}장까지 업로드할 수 있습니다.`);
        return;
      }

      const file = e.target.files[0];
      const reader = new FileReader();

      reader.onload = (event) => {
        if (event.target && typeof event.target.result === "string") {
          setImages([...images, event.target.result]);
        }
      };

      reader.readAsDataURL(file);
    }
  };

  const removeImage = (index: number) => {
    setImages(images.filter((_, i) => i !== index));
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (images.length === 0) {
      newErrors.images = "프로필 사진을 1장 이상 업로드해주세요.";
    }

    if (!name.trim()) {
      newErrors.name = "이름을 입력해주세요.";
    }

    if (!bio.trim()) {
      newErrors.bio = "소개글을 입력해주세요.";
    }

    if (!gender) {
      newErrors.gender = "성별을 선택해주세요.";
    }

    if (Number.isNaN(Number(age)) || Number(age) < 18 || Number(age) > 100) {
      newErrors.age = "유효한 나이를 입력해주세요. (18-100)";
    }

    if (
      Number.isNaN(Number(height)) ||
      Number(height) < 140 ||
      Number(height) > 220
    ) {
      newErrors.height = "유효한 키를 입력해주세요. (140-220cm)";
    }

    if (
      Number.isNaN(Number(weight)) ||
      Number(weight) < 30 ||
      Number(weight) > 150
    ) {
      newErrors.weight = "유효한 몸무게를 입력해주세요. (30-150kg)";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleConfirm = () => {
    if (validateForm() && gender) {
      onComplete({
        images,
        name,
        bio,
        gender,
        age,
        height,
        weight,
        email,
      });
    }
  };

  return (
    <div className="h-full flex flex-col">
      {/* Content */}
      <div className="flex-1 overflow-auto py-4 px-1 overflow-y-scroll [&::-webkit-scrollbar]:hidden scrollbar-width-none">
        <div className="space-y-6">
          {/* Image upload */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              프로필 사진 (1~5장)
            </label>
            <div className="flex flex-wrap gap-2">
              {images.map((image, index) => (
                <div
                  key={index}
                  className="relative w-24 h-24 rounded-lg overflow-hidden bg-gray-100"
                >
                  <img
                    src={image || "/placeholder.svg"}
                    alt={`Profile ${index + 1}`}
                    className="w-full h-full object-cover"
                  />
                  <button
                    type="button"
                    onClick={() => removeImage(index)}
                    className="absolute top-1 right-1 w-5 h-5 rounded-full bg-black/50 flex items-center justify-center text-white"
                  >
                    <X size={12} />
                  </button>
                </div>
              ))}

              {images.length < maxImages && (
                <label className="w-24 h-24 rounded-lg border-2 border-dashed border-gray-300 flex flex-col items-center justify-center cursor-pointer bg-white hover:bg-gray-50">
                  <Camera size={24} className="text-gray-400 mb-1" />
                  <span className="text-xs text-gray-500">사진 추가</span>
                  <input
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handleImageUpload}
                  />
                </label>
              )}
            </div>
            {errors.images && (
              <p className="mt-1 text-sm text-red-500">{errors.images}</p>
            )}
          </div>

          {/* Name */}
          <div>
            <label
              htmlFor="name"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              이름
            </label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className={cn(
                "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                errors.name ? "border-red-300" : "border-gray-300",
              )}
              placeholder="실명을 입력해주세요"
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-500">{errors.name}</p>
            )}
          </div>

          {/* Email */}
          <div>
            <label
              htmlFor="email"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              이메일
            </label>
            <input
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={cn(
                "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                errors.email ? "border-red-300" : "border-gray-300",
              )}
              placeholder="이메일을 입력해주세요"
            />
            {errors.email && (
              <p className="mt-1 text-sm text-red-500">{errors.email}</p>
            )}
          </div>

          {/* Bio */}
          <div>
            <label
              htmlFor="bio"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              소개글
            </label>
            <textarea
              id="bio"
              value={bio}
              onChange={(e) => setBio(e.target.value)}
              rows={4}
              className={cn(
                "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                errors.bio ? "border-red-300" : "border-gray-300",
              )}
              placeholder="자신을 소개해주세요"
            />
            {errors.bio && (
              <p className="mt-1 text-sm text-red-500">{errors.bio}</p>
            )}
          </div>

          {/* Gender */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              성별
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setGender("male")}
                className={cn(
                  "py-2 rounded-lg border font-medium",
                  gender === "male"
                    ? "bg-black text-white border-black"
                    : "bg-white text-gray-700 border-gray-300",
                )}
              >
                남성
              </button>
              <button
                type="button"
                onClick={() => setGender("female")}
                className={cn(
                  "py-2 rounded-lg border font-medium",
                  gender === "female"
                    ? "bg-black text-white border-black"
                    : "bg-white text-gray-700 border-gray-300",
                )}
              >
                여성
              </button>
            </div>
            {errors.gender && (
              <p className="mt-1 text-sm text-red-500">{errors.gender}</p>
            )}
          </div>

          {/* Age */}
          <div>
            <label
              htmlFor="age"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              나이
            </label>
            <div className="relative">
              <input
                type="number"
                id="age"
                value={age}
                onChange={(e) => setAge(Number(e.target.value))}
                className={cn(
                  "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                  errors.age ? "border-red-300" : "border-gray-300",
                )}
                placeholder="나이를 입력해주세요"
                min="18"
                max="100"
              />
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <span className="text-gray-500">세</span>
              </div>
            </div>
            {errors.age && (
              <p className="mt-1 text-sm text-red-500">{errors.age}</p>
            )}
          </div>

          {/* Height */}
          <div>
            <label
              htmlFor="height"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              키
            </label>
            <div className="relative">
              <input
                type="number"
                id="height"
                value={height}
                onChange={(e) => setHeight(Number(e.target.value))}
                className={cn(
                  "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                  errors.height ? "border-red-300" : "border-gray-300",
                )}
                placeholder="키를 입력해주세요"
                min="140"
                max="220"
              />
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <span className="text-gray-500">cm</span>
              </div>
            </div>
            {errors.height && (
              <p className="mt-1 text-sm text-red-500">{errors.height}</p>
            )}
          </div>

          {/* Weight */}
          <div>
            <label
              htmlFor="weight"
              className="block text-sm font-medium text-gray-700 mb-2"
            >
              몸무게
            </label>
            <div className="relative">
              <input
                type="number"
                id="weight"
                value={weight}
                onChange={(e) => setWeight(Number(e.target.value))}
                className={cn(
                  "w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/20",
                  errors.weight ? "border-red-300" : "border-gray-300",
                )}
                placeholder="몸무게를 입력해주세요"
                min="30"
                max="150"
              />
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                <span className="text-gray-500">kg</span>
              </div>
            </div>
            {errors.weight && (
              <p className="mt-1 text-sm text-red-500">{errors.weight}</p>
            )}
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="p-4 border-t border-gray-100 bg-white">
        <Button onClick={handleConfirm} className="w-full">
          완료
        </Button>
      </div>
    </div>
  );
};

import { AppScreen } from "@stackflow/plugin-basic-ui";
import { Logo } from "../components/Logo";
import { KaKaoLoginButton } from "../components/KaKaoLoginButton";

export const LoginActivity = () => {
  const handleKaKaoLoginClick = () => {};

  return (
    <AppScreen>
      <div className="h-[100vh] flex flex-col items-center justify-between bg-white p-6">
        {/* Top logo area */}
        <div className="w-full flex justify-center mt-12">
          <Logo showText={true} className="scale-125" />
        </div>

        {/* Middle content area with tagline */}
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-3">가치관으로 연결되는 만남</h1>
          <p className="text-gray-500 mb-8">
            비슷한 가치관과 관심사를 가진 사람들과
            <br />
            자연스럽게 연결되세요
          </p>
        </div>

        {/* Bottom login button area */}
        <div className="w-full mb-12">
          <KaKaoLoginButton handleClick={handleKaKaoLoginClick} />

          <p className="text-xs text-center text-gray-400 mt-4">
            로그인 시 <span className="underline">이용약관</span>과{" "}
            <span className="underline">개인정보처리방침</span>에 동의하게
            됩니다
          </p>
        </div>
      </div>
    </AppScreen>
  );
};

export const KaKaoLoginButton = ({
  handleClick,
}: {
  handleClick: () => void;
}) => {
  return (
    <button
      onClick={handleClick}
      className="w-full py-3 rounded-md bg-[#FEE500] text-[#191919] font-medium flex items-center justify-center"
    >
      <div className="mr-2">
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            fillRule="evenodd"
            clipRule="evenodd"
            d="M12 4C7.58172 4 4 6.8147 4 10.2C4 12.4256 5.42456 14.3835 7.64031 15.4978C7.40388 16.2887 6.91373 17.5919 6.67196 18.1216C6.37867 18.7864 6.73478 18.7347 7.05682 18.5252C7.30379 18.3579 8.86514 17.3559 9.73754 16.7563C10.4693 16.9193 11.2239 17 12 17C16.4183 17 20 14.1853 20 10.2C20 6.8147 16.4183 4 12 4Z"
            fill="#191919"
          />
        </svg>
      </div>
      카카오로 시작하기
    </button>
  );
};

import type React from "react";
import { cn } from "../utils/classNaem";

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "default";
  size?: "sm" | "md" | "lg";
  fullWidth?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  isLoading?: boolean;
  ref?: React.Ref<HTMLButtonElement>;
}

export const Button = ({
  className = "",
  variant = "default",
  size = "md",
  fullWidth = false,
  leftIcon,
  rightIcon,
  isLoading = false,
  disabled,
  children,
  ref,
  ...props
}: ButtonProps) => {
  // Variant styles
  const variantStyles = {
    default: "bg-black text-white hover:bg-black/90",
  };

  // Size styles
  const sizeStyles = {
    sm: "py-1.5 px-3 text-sm",
    md: "py-2.5 px-4",
    lg: "py-3 px-5 text-lg",
  };

  return (
    <button
      ref={ref}
      className={cn(
        "rounded-lg font-medium flex items-center justify-center transition-colors",
        variantStyles[variant],
        sizeStyles[size],
        fullWidth ? "w-full" : "",
        disabled || isLoading ? "opacity-50 cursor-not-allowed" : "",
        className,
      )}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && (
        <svg
          className="animate-spin -ml-1 mr-2 h-4 w-4 text-current"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          ></circle>
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          ></path>
        </svg>
      )}

      {!isLoading && leftIcon && <span className="mr-2">{leftIcon}</span>}
      {children}
      {!isLoading && rightIcon && <span className="ml-2">{rightIcon}</span>}
    </button>
  );
};

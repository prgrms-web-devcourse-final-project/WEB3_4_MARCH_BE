export const getCurrentPosition = (options?: {
  enableHighAccuracy?: boolean;
  timeout?: number;
  maximumAge?: number;
  retryPermission?: boolean;
}) => {
  return new Promise<GeolocationPosition | GeolocationPositionError | null>(
    (resolve, reject) => {
      const geoOptions: PositionOptions = {
        enableHighAccuracy: options?.enableHighAccuracy || false,
        timeout: options?.timeout || 10000,
        maximumAge: options?.maximumAge || 0,
      };

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve(position);
        },
        (error) => {
          // If permission denied and retry is requested
          if (
            error.code === GeolocationPositionError.PERMISSION_DENIED &&
            options?.retryPermission
          ) {
            // Return the error so the UI can show a custom prompt to the user
            resolve(error);
          } else {
            resolve(null);
          }
        },
        geoOptions,
      );
    },
  );
};

// Helper function to check if error is a permission denied error
export const isPermissionDeniedError = (
  error: GeolocationPositionError | unknown,
): boolean => {
  return (
    error instanceof GeolocationPositionError &&
    error.code === GeolocationPositionError.PERMISSION_DENIED
  );
};

// Get status of geolocation permission
export const getGeolocationPermissionStatus =
  async (): Promise<PermissionState> => {
    try {
      const permission = await navigator.permissions.query({
        name: "geolocation" as PermissionName,
      });
      return permission.state;
    } catch (error) {
      console.error("Error checking geolocation permission:", error);
      return "denied";
    }
  };

/**
 * Shows a user-friendly dialog explaining why location permissions are needed
 * and provides options to retry or cancel.
 *
 * @returns A promise that resolves to true if user wants to retry, false otherwise
 */
export const showLocationPermissionDialog = (): Promise<boolean> => {
  return new Promise((resolve) => {
    // Create dialog elements
    const dialogOverlay = document.createElement("div");
    const dialogContainer = document.createElement("div");
    const dialogTitle = document.createElement("h3");
    const dialogContent = document.createElement("p");
    const buttonContainer = document.createElement("div");
    const retryButton = document.createElement("button");
    const cancelButton = document.createElement("button");

    // Set content
    dialogTitle.textContent = "위치 정보 접근 필요";
    dialogContent.textContent =
      "이 앱은 정확한 서비스를 제공하기 위해 위치 정보 접근이 필요합니다. " +
      "브라우저에서 위치 접근 권한을 허용해주세요.";
    retryButton.textContent = "허용";
    cancelButton.textContent = "취소";

    // Style the dialog
    Object.assign(dialogOverlay.style, {
      position: "fixed",
      top: "0",
      left: "0",
      right: "0",
      bottom: "0",
      backgroundColor: "rgba(0, 0, 0, 0.5)",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      zIndex: "9999",
    });

    Object.assign(dialogContainer.style, {
      backgroundColor: "white",
      borderRadius: "8px",
      padding: "24px",
      maxWidth: "450px",
      width: "90%",
      boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    });

    Object.assign(dialogTitle.style, {
      margin: "0 0 16px 0",
      fontSize: "20px",
      fontWeight: "bold",
    });

    Object.assign(dialogContent.style, {
      marginBottom: "24px",
      lineHeight: "1.5",
    });

    Object.assign(buttonContainer.style, {
      display: "flex",
      justifyContent: "flex-end",
      gap: "12px",
    });

    Object.assign(retryButton.style, {
      padding: "8px 16px",
      backgroundColor: "#007bff",
      color: "white",
      border: "none",
      borderRadius: "4px",
      cursor: "pointer",
      fontWeight: "bold",
    });

    Object.assign(cancelButton.style, {
      padding: "8px 16px",
      backgroundColor: "#f8f9fa",
      border: "1px solid #dee2e6",
      borderRadius: "4px",
      cursor: "pointer",
    });

    // Add event listeners
    retryButton.addEventListener("click", () => {
      document.body.removeChild(dialogOverlay);
      showBrowserPermissionGuide();
      resolve(true);
    });

    cancelButton.addEventListener("click", () => {
      document.body.removeChild(dialogOverlay);
      resolve(false);
    });

    // Assemble the dialog
    buttonContainer.appendChild(cancelButton);
    buttonContainer.appendChild(retryButton);
    dialogContainer.appendChild(dialogTitle);
    dialogContainer.appendChild(dialogContent);
    dialogContainer.appendChild(buttonContainer);
    dialogOverlay.appendChild(dialogContainer);

    // Add to document
    document.body.appendChild(dialogOverlay);
  });
};

// Optional helper function to handle the complete flow of requesting location
export const requestLocationWithRetryFlow = async (options?: {
  enableHighAccuracy?: boolean;
  timeout?: number;
  maximumAge?: number;
}): Promise<GeolocationPosition | null> => {
  // First attempt
  const position = await getCurrentPosition(options);

  // If successful, return the position
  if (position && !(position instanceof GeolocationPositionError)) {
    return position;
  }

  // If it failed due to permission denied, show dialog
  if (
    position instanceof GeolocationPositionError &&
    position.code === GeolocationPositionError.PERMISSION_DENIED
  ) {
    // Show dialog and wait for user decision
    const shouldRetry = await showLocationPermissionDialog();

    if (shouldRetry) {
      // Try again with retry flag
      const retryPosition = await getCurrentPosition({
        ...options,
        retryPermission: true,
      });

      if (
        retryPosition &&
        !(retryPosition instanceof GeolocationPositionError)
      ) {
        return retryPosition;
      }
    }
  }

  // If we get here, we couldn't get the position
  return null;
};

/**
 * Shows a browser-specific guide on how to reset location permissions
 * This dialog appears when permissions are already denied
 *
 * @returns A promise that resolves when the dialog is closed
 */
export const showBrowserPermissionGuide = (): Promise<void> => {
  return new Promise((resolve) => {
    // Create dialog elements
    const dialogOverlay = document.createElement("div");
    const dialogContainer = document.createElement("div");
    const dialogTitle = document.createElement("h3");
    const dialogContent = document.createElement("p");
    const browserGuide = document.createElement("div");
    const closeButton = document.createElement("button");

    // Detect browser
    const isChrome = navigator.userAgent.indexOf("Chrome") > -1;
    const isFirefox = navigator.userAgent.indexOf("Firefox") > -1;
    const isSafari = navigator.userAgent.indexOf("Safari") > -1 && !isChrome;
    const isEdge = navigator.userAgent.indexOf("Edg") > -1;

    // Set content
    dialogTitle.textContent = "위치 권한 설정 변경 필요";
    dialogContent.textContent =
      "위치 권한이 이미 거부되어 있습니다. 아래 안내에 따라 브라우저 설정에서 권한을 변경해주세요:";

    // Create browser-specific instructions
    let instructions = "";
    if (isChrome || isEdge) {
      instructions = `
        <strong>Chrome/Edge 브라우저</strong>:
        <ol>
          <li>주소창 왼쪽의 자물쇠(또는 느낌표) 아이콘을 클릭하세요.</li>
          <li>"위치" 설정을 찾아 "허용"으로 변경하세요.</li>
          <li>페이지를 새로고침하세요.</li>
        </ol>
      `;
    } else if (isFirefox) {
      instructions = `
        <strong>Firefox 브라우저</strong>:
        <ol>
          <li>주소창 왼쪽의 자물쇠 아이콘을 클릭하세요.</li>
          <li>"권한 설정" 버튼을 클릭하세요.</li>
          <li>"위치 정보 접근"을 "허용"으로 변경하세요.</li>
          <li>페이지를 새로고침하세요.</li>
        </ol>
      `;
    } else if (isSafari) {
      instructions = `
        <strong>Safari 브라우저</strong>:
        <ol>
          <li>Safari 메뉴 > "이 웹 사이트에 대한 설정"을 클릭하세요.</li>
          <li>"위치" 설정을 "허용"으로 변경하세요.</li>
          <li>페이지를 새로고침하세요.</li>
        </ol>
      `;
    } else {
      instructions = `
        <ol>
          <li>브라우저 설정에서 위치 권한을 허용해주세요.</li>
          <li>설정 변경 후 페이지를 새로고침하세요.</li>
        </ol>
      `;
    }

    browserGuide.innerHTML = instructions;
    closeButton.textContent = "확인";

    // Style the dialog
    Object.assign(dialogOverlay.style, {
      position: "fixed",
      top: "0",
      left: "0",
      right: "0",
      bottom: "0",
      backgroundColor: "rgba(0, 0, 0, 0.5)",
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
      zIndex: "9999",
    });

    Object.assign(dialogContainer.style, {
      backgroundColor: "white",
      borderRadius: "8px",
      padding: "24px",
      maxWidth: "500px",
      width: "90%",
      boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    });

    Object.assign(dialogTitle.style, {
      margin: "0 0 16px 0",
      fontSize: "20px",
      fontWeight: "bold",
    });

    Object.assign(dialogContent.style, {
      marginBottom: "16px",
      lineHeight: "1.5",
    });

    Object.assign(browserGuide.style, {
      marginBottom: "24px",
      lineHeight: "1.6",
    });

    Object.assign(closeButton.style, {
      padding: "8px 20px",
      backgroundColor: "#007bff",
      color: "white",
      border: "none",
      borderRadius: "4px",
      cursor: "pointer",
      fontWeight: "bold",
      display: "block",
      marginLeft: "auto",
    });

    // Add event listener
    closeButton.addEventListener("click", () => {
      document.body.removeChild(dialogOverlay);
      resolve();
    });

    // Assemble the dialog
    dialogContainer.appendChild(dialogTitle);
    dialogContainer.appendChild(dialogContent);
    dialogContainer.appendChild(browserGuide);
    dialogContainer.appendChild(closeButton);
    dialogOverlay.appendChild(dialogContainer);

    // Add to document
    document.body.appendChild(dialogOverlay);
  });
};

/**
 * Enhanced location request flow that handles already denied permissions
 * by showing browser-specific permission reset instructions
 *
 * @param options - Geolocation options
 * @returns A promise that resolves to the position or null
 */
export const requestLocationWithPermissionGuide = async (options?: {
  enableHighAccuracy?: boolean;
  timeout?: number;
  maximumAge?: number;
}): Promise<GeolocationPosition | null> => {
  // First check current permission status
  const permissionStatus = await getGeolocationPermissionStatus();

  // If permission is already denied, show browser guide
  if (permissionStatus === "denied") {
    await showBrowserPermissionGuide();
    return null;
  }

  // Otherwise use normal flow with retry
  return requestLocationWithRetryFlow(options);
};

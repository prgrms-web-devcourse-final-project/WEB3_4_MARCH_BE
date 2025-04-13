import { useEffect, useState } from "react";
import { useMap } from "./contexts/MapContext";

export interface CustomOverlayProps {
  /**
   * Overlay position (latitude and longitude)
   */
  position: {
    lat: number;
    lng: number;
  };
  /**
   * Overlay content (HTML or text)
   */
  content: string;
  /**
   * Overlay z-index
   */
  zIndex?: number;
  /**
   * Y-axis offset from the position
   */
  yAnchor?: number;
  /**
   * X-axis offset from the position
   */
  xAnchor?: number;
  /**
   * Click handler for the overlay
   */
  onClick?: () => void;
}

export const CustomOverlay: React.FC<CustomOverlayProps> = ({
  position,
  content,
  zIndex,
  yAnchor = 1.0,
  xAnchor = 0.5,
  onClick,
}) => {
  const { mapInstance } = useMap();
  const [overlay, setOverlay] = useState<kakao.maps.CustomOverlay | null>(null);

  // Create and set up the overlay
  useEffect(() => {
    if (!mapInstance) return;

    // Create overlay position
    const overlayPosition = new window.kakao.maps.LatLng(
      position.lat,
      position.lng,
    );

    // Configure overlay options
    const options: kakao.maps.CustomOverlayOptions = {
      position: overlayPosition,
      content: content,
      zIndex: zIndex,
      yAnchor: yAnchor,
      xAnchor: xAnchor,
    };

    // Create the overlay
    const newOverlay = new window.kakao.maps.CustomOverlay(options);

    // Add the overlay to the map
    newOverlay.setMap(mapInstance);
    setOverlay(newOverlay);

    // Cleanup function
    return () => {
      if (newOverlay) newOverlay.setMap(null);
    };
  }, [
    mapInstance,
    position.lat,
    position.lng,
    content,
    zIndex,
    yAnchor,
    xAnchor,
  ]);

  // Handle position updates
  useEffect(() => {
    if (!mapInstance || !overlay) return;

    const overlayPosition = new window.kakao.maps.LatLng(
      position.lat,
      position.lng,
    );
    overlay.setPosition(overlayPosition);
  }, [mapInstance, overlay, position.lat, position.lng]);

  // Add click event listener
  useEffect(() => {
    if (!overlay || !onClick) return;

    // Get the content element and add the click event
    const contentElement = overlay.getContent();

    if (typeof contentElement === "string") {
      return; // Can't add event to string content
    }

    if (contentElement instanceof Element) {
      const listener = () => {
        onClick();
      };

      contentElement.addEventListener("click", listener);

      return () => {
        contentElement.removeEventListener("click", listener);
      };
    }
  }, [overlay, onClick]);

  // This component doesn't render anything directly
  return null;
};

import { useEffect, useState, useRef } from "react";
import { useMap } from "./contexts/MapContext";

export interface MarkerProps {
  /**
   * Marker position (latitude and longitude)
   */
  position: {
    lat: number;
    lng: number;
  };
  /**
   * Marker image URL (optional)
   */
  image?: {
    src: string;
    size: {
      width: number;
      height: number;
    };
    options?: {
      offset?: {
        x: number;
        y: number;
      };
      alt?: string;
      clickable?: boolean;
    };
  };
  /**
   * Marker click handler
   */
  onClick?: () => void;
  /**
   * Marker drag event handlers
   */
  draggable?: boolean;
  onDragStart?: () => void;
  onDragEnd?: (position: { lat: number; lng: number }) => void;
  /**
   * Marker z-index
   */
  zIndex?: number;
  /**
   * Marker opacity (0-1)
   */
  opacity?: number;
  /**
   * Marker title (hover text)
   */
  title?: string;
  /**
   * Marker content (when clicked)
   */
  content?: React.ReactNode;
}

export const Marker: React.FC<MarkerProps> = ({
  position,
  image,
  onClick,
  draggable = false,
  onDragStart,
  onDragEnd,
  zIndex,
  opacity,
  title,
  content,
}) => {
  const { mapInstance } = useMap();
  const [marker, setMarker] = useState<kakao.maps.Marker | null>(null);
  const [infoWindow, setInfoWindow] = useState<kakao.maps.InfoWindow | null>(
    null,
  );

  // Create and set up the marker
  useEffect(() => {
    if (!mapInstance) return;

    // Create marker position
    const markerPosition = new window.kakao.maps.LatLng(
      position.lat,
      position.lng,
    );

    // Configure marker options
    const options: kakao.maps.MarkerOptions = {
      position: markerPosition,
      draggable,
      zIndex,
      opacity,
      title,
    };

    // Add custom image if provided
    if (image) {
      const markerSize = new window.kakao.maps.Size(
        image.size.width,
        image.size.height,
      );
      const markerImage = new window.kakao.maps.MarkerImage(
        image.src,
        markerSize,
      );

      if (image.options) {
        if (image.options.offset) {
          const offset = new window.kakao.maps.Point(
            image.options.offset.x,
            image.options.offset.y,
          );
          options.image = new window.kakao.maps.MarkerImage(
            image.src,
            markerSize,
            { offset },
          );
        } else {
          options.image = markerImage;
        }
      } else {
        options.image = markerImage;
      }
    }

    // Create the marker
    const newMarker = new window.kakao.maps.Marker(options);

    // Add the marker to the map
    newMarker.setMap(mapInstance);
    setMarker(newMarker);

    // Create info window if content is provided
    let infoWindowInstance: kakao.maps.InfoWindow | null = null;
    if (content && typeof content === "string") {
      infoWindowInstance = new window.kakao.maps.InfoWindow({
        content: content,
        removable: true,
      });
      setInfoWindow(infoWindowInstance);
    }

    // Cleanup function
    return () => {
      if (newMarker) newMarker.setMap(null);
      if (infoWindowInstance) infoWindowInstance.close();
    };
  }, [
    mapInstance,
    position.lat,
    position.lng,
    draggable,
    zIndex,
    opacity,
    title,
    image,
    content,
  ]);

  // Handle position updates
  useEffect(() => {
    if (!mapInstance || !marker) return;

    const markerPosition = new window.kakao.maps.LatLng(
      position.lat,
      position.lng,
    );
    marker.setPosition(markerPosition);
  }, [mapInstance, marker, position.lat, position.lng]);

  // Add click event listener
  useEffect(() => {
    if (!marker) return;

    if (onClick || (content && infoWindow)) {
      const clickListener = (mouseEvent: kakao.maps.event.MouseEvent) => {
        // Open info window if content exists
        if (content && infoWindow && mapInstance) {
          infoWindow.open(mapInstance, marker);
        }

        // Call the custom onClick handler if provided
        if (onClick) onClick();
      };

      window.kakao.maps.event.addListener(marker, "click", clickListener);

      return () => {
        window.kakao.maps.event.removeListener(marker, "click", clickListener);
      };
    }
  }, [marker, mapInstance, onClick, content, infoWindow]);

  // Add drag event listeners
  useEffect(() => {
    if (!marker || !draggable) return;

    if (onDragStart) {
      const dragStartListener = () => {
        onDragStart();
      };

      window.kakao.maps.event.addListener(
        marker,
        "dragstart",
        dragStartListener,
      );

      return () => {
        window.kakao.maps.event.removeListener(
          marker,
          "dragstart",
          dragStartListener,
        );
      };
    }
  }, [marker, draggable, onDragStart]);

  useEffect(() => {
    if (!marker || !draggable) return;

    if (onDragEnd) {
      const dragEndListener = () => {
        const position = marker.getPosition();
        onDragEnd({
          lat: position.getLat(),
          lng: position.getLng(),
        });
      };

      window.kakao.maps.event.addListener(marker, "dragend", dragEndListener);

      return () => {
        window.kakao.maps.event.removeListener(
          marker,
          "dragend",
          dragEndListener,
        );
      };
    }
  }, [marker, draggable, onDragEnd]);

  // Update marker options when they change
  useEffect(() => {
    if (!marker) return;

    if (draggable !== undefined) marker.setDraggable(draggable);
    if (zIndex !== undefined) marker.setZIndex(zIndex);
    if (opacity !== undefined) marker.setOpacity(opacity);
  }, [marker, draggable, zIndex, opacity]);

  // This component doesn't render anything directly
  return null;
};

import { useEffect, useRef } from "react";
import type { ReactNode } from "react";
import { useMap } from "./contexts/MapContext";

interface KakaoMapProps {
  width?: string;
  height?: string;
  center?: { lat: number; lng: number };
  level?: number;
  children?: ReactNode;
}

const KakaoMap: React.FC<KakaoMapProps> = ({
  width = "100%",
  height = "100%",
  center = { lat: 37.5665, lng: 126.978 }, // Default to Seoul
  level = 3,
  children,
}) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const { mapInstance, setMapInstance } = useMap();

  useEffect(() => {
    if (!mapRef.current) return;

    // Initialize the map
    const options = {
      center: new window.kakao.maps.LatLng(center.lat, center.lng),
      level,
    };

    const map = new window.kakao.maps.Map(mapRef.current, options);

    // Set map in context instead of local ref
    setMapInstance(map);

    // Add controls
    const zoomControl = new window.kakao.maps.ZoomControl();
    map.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT);

    const mapTypeControl = new window.kakao.maps.MapTypeControl();
    map.addControl(mapTypeControl, window.kakao.maps.ControlPosition.TOPRIGHT);

    return () => {
      setMapInstance(null);
    };
  }, [center.lat, center.lng, level, setMapInstance]);

  // Update map center when the center prop changes
  useEffect(() => {
    if (!mapInstance) return;

    const newCenter = new window.kakao.maps.LatLng(center.lat, center.lng);
    mapInstance.setCenter(newCenter);
  }, [center.lat, center.lng, mapInstance]);

  // Update map level when the level prop changes
  useEffect(() => {
    if (!mapInstance) return;

    mapInstance.setLevel(level);
  }, [level, mapInstance]);

  return (
    <div ref={mapRef} style={{ width, height }}>
      {/* Render children (Markers, etc.) when map is available */}
      {mapInstance && children}
    </div>
  );
};

export default KakaoMap;

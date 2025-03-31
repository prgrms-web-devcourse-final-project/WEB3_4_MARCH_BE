import { useEffect, useRef } from "react";

interface KakaoMapProps {
  width?: string;
  height?: string;
  center?: { lat: number; lng: number };
  level?: number;
}

const KakaoMap: React.FC<KakaoMapProps> = ({
  width = "100%",
  height = "100%",
  center = { lat: 37.5665, lng: 126.978 }, // Default to Seoul
  level = 3,
}) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<kakao.maps.KakaoMap | null>(null);

  useEffect(() => {
    if (!mapRef.current) return;

    // Initialize the map
    const options = {
      center: new window.kakao.maps.LatLng(center.lat, center.lng),
      level,
    };

    const map = new window.kakao.maps.Map(mapRef.current, options);
    mapInstanceRef.current = map;

    // Add controls
    const zoomControl = new window.kakao.maps.ZoomControl();
    map.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT);

    const mapTypeControl = new window.kakao.maps.MapTypeControl();
    map.addControl(mapTypeControl, window.kakao.maps.ControlPosition.TOPRIGHT);

    return () => {
      mapInstanceRef.current = null;
    };
  }, [center.lat, center.lng, level]);

  // Update map center when the center prop changes
  useEffect(() => {
    if (!mapInstanceRef.current) return;

    const newCenter = new window.kakao.maps.LatLng(center.lat, center.lng);
    mapInstanceRef.current.setCenter(newCenter);
  }, [center.lat, center.lng]);

  // Update map level when the level prop changes
  useEffect(() => {
    if (!mapInstanceRef.current) return;

    mapInstanceRef.current.setLevel(level);
  }, [level]);

  return <div ref={mapRef} style={{ width, height }} />;
};

export default KakaoMap;

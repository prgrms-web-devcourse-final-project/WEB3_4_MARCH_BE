import { MapPin } from "lucide-react";
import { useMap } from "./kakaomap/contexts/MapContext";
import KakaoMap from "./kakaomap/KakaoMap";

export const MapView = () => {
  const { mapInstance, loading } = useMap();

  const handleMoveToCurrentLocation = () => {
    window.navigator.geolocation.getCurrentPosition((position) => {
      if (mapInstance) {
        mapInstance.panTo(
          new window.kakao.maps.LatLng(
            position.coords.latitude,
            position.coords.longitude,
          ),
        );
      }
    });
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="relative h-full">
      <KakaoMap />
      <div className="absolute bottom-6 right-4 z-10">
        <button
          className="w-12 h-12 rounded-full bg-white shadow-lg flex items-center justify-center"
          onClick={handleMoveToCurrentLocation}
        >
          <MapPin className="w-5 h-5 text-primary" />
        </button>
      </div>
    </div>
  );
};

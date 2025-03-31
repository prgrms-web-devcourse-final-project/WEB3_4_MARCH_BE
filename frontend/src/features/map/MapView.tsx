import { MapPin } from "lucide-react";
import { useMap } from "./kakaomap/contexts/MapContext";
import KakaoMap from "./kakaomap/KakaoMap";

export const MapView = () => {
  const { loading, mapInstance } = useMap();

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

  return (
    <div className="relative h-full">
      {loading ? (
        <div>Loading...</div>
      ) : (
        <KakaoMap
          center={{
            lat: 37.5665,
            lng: 126.978, // Default to Seoul
          }}
          height="100%"
          width="100%"
        />
      )}
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

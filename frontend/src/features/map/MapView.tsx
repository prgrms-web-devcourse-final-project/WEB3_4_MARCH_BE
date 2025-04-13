import { MapPin } from "lucide-react";
import { useMap } from "./kakaomap/contexts/MapContext";
import KakaoMap from "./kakaomap/KakaoMap";
import { Loading } from "../../components/Loading";
import {
  getCurrentPosition,
  isPermissionDeniedError,
} from "../../utils/currentPosition";

export const MapView = () => {
  const { mapInstance, loading } = useMap();

  const handleMoveToCurrentLocation = async () => {
    const positionResult = await getCurrentPosition();

    if (!positionResult || isPermissionDeniedError(positionResult)) {
      return;
    }

    const position = positionResult as GeolocationPosition;

    if (mapInstance) {
      mapInstance.panTo(
        new window.kakao.maps.LatLng(
          position.coords.latitude,
          position.coords.longitude,
        ),
      );

      mapInstance.setLevel(3);
    }
  };

  if (loading) {
    return (
      <div className="h-full w-full flex flex-col items-center justify-center">
        <Loading text="지도를 불러오고 있습니다..." />
      </div>
    );
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

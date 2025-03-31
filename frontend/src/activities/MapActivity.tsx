import { useKakaoLoader } from "react-kakao-maps-sdk";
import AppScreenLayout from "../layout/AppScreenLayout";
import { MapPin } from "lucide-react";
import KakaoMap from "../components/KakaoMap";

type MapActivityProps = {
  params: {};
};

const MapActivity: React.FC<MapActivityProps> = ({ params: {} }) => {
  const [loading] = useKakaoLoader({
    appkey: import.meta.env.VITE_DEFAULT_KAKAO_API_KEY,
  });

  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
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
    </AppScreenLayout>
  );
};

export default MapActivity;

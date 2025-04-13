import { MapPin, Move } from "lucide-react";
import { useMap } from "./kakaomap/contexts/MapContext";
import KakaoMap from "./kakaomap/KakaoMap";
import { Loading } from "../../components/Loading";
import {
  getCurrentPosition,
  isPermissionDeniedError,
} from "../../utils/currentPosition";
import { TextMarker } from "./kakaomap";
import type { RecommendedUserDto } from "../../api/__generated__";
import { useEffect, useRef } from "react";

export const MapView = ({
  matchings,
}: { matchings: RecommendedUserDto[] | undefined }) => {
  const { mapInstance, loading } = useMap();
  const boundsRef = useRef<kakao.maps.LatLngBounds | null>(null);

  // Initialize bounds and fit all markers within view
  useEffect(() => {
    if (!mapInstance || !matchings || matchings.length === 0) return;

    // Create a new bounds object
    const bounds = new window.kakao.maps.LatLngBounds();

    // Add all valid matching positions to the bounds
    for (const matching of matchings) {
      if (isValid(matching)) {
        const position = new window.kakao.maps.LatLng(
          matching.latitude,
          matching.longitude,
        );
        bounds.extend(position);
      }
    }

    // Store the bounds in a ref for use with the fit bounds button
    boundsRef.current = bounds;

    // If we have valid bounds with points, fit the map to these bounds
    if (!bounds.isEmpty()) {
      mapInstance.setBounds(bounds);
    }
  }, [mapInstance, matchings]);

  const handleFitBounds = () => {
    if (!mapInstance || !boundsRef.current || boundsRef.current.isEmpty())
      return;

    // Fit all markers within the view
    mapInstance.setBounds(boundsRef.current);
  };

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
      <KakaoMap>
        {matchings?.map((matching) => {
          if (isValid(matching)) {
            return (
              <TextMarker
                key={matching.id}
                position={{ lat: matching.latitude, lng: matching.longitude }}
                text={matching.nickname || "사용자"}
                title={matching.nickname || "사용자"}
              />
            );
          }
          return null;
        })}
      </KakaoMap>
      <div className="absolute bottom-6 right-4 z-10 flex flex-col gap-2">
        <button
          className="w-12 h-12 rounded-full bg-white shadow-lg flex items-center justify-center"
          onClick={handleFitBounds}
          title="모든 마커 보기"
        >
          <Move className="w-5 h-5 text-primary" />
        </button>
        <button
          className="w-12 h-12 rounded-full bg-white shadow-lg flex items-center justify-center"
          onClick={handleMoveToCurrentLocation}
          title="내 위치로 이동"
        >
          <MapPin className="w-5 h-5 text-primary" />
        </button>
      </div>
    </div>
  );
};

// Type guard to ensure latitude and longitude are defined numbers
const isValid = (
  matching: RecommendedUserDto,
): matching is RecommendedUserDto & {
  latitude: number;
  longitude: number;
} => {
  return (
    typeof matching.latitude === "number" &&
    typeof matching.longitude === "number"
  );
};

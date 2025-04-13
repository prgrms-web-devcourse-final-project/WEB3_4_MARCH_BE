import AppScreenLayout from "../layout/AppScreenLayout";

import { MapView } from "../features/map/MapView";
import { MapProvider } from "../features/map/kakaomap/contexts/MapContext";
import { useMatchings } from "../features/matching/useMatchings";
import { DumyMatchings } from "../features/matching/dumy";

type MapActivityProps = {
  params: {};
};

const MapActivity: React.FC<MapActivityProps> = ({ params: {} }) => {
  const { data, isLoading, isError } = useMatchings();

  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
      <MapProvider>
        <MapView matchings={DumyMatchings} />
      </MapProvider>
    </AppScreenLayout>
  );
};

export default MapActivity;

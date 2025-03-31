import AppScreenLayout from "../layout/AppScreenLayout";

import { MapView } from "../features/map/MapView";
import { MapProvider } from "../features/map/kakaomap/contexts/MapContext";

type MapActivityProps = {
  params: {};
};

const MapActivity: React.FC<MapActivityProps> = ({ params: {} }) => {
  return (
    <AppScreenLayout title="CONNECT TO" wideScreen>
      <MapProvider>
        <MapView />
      </MapProvider>
    </AppScreenLayout>
  );
};

export default MapActivity;

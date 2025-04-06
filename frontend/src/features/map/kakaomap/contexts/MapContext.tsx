import { createContext, useContext, useState, type ReactNode } from "react";
import { useKakaoLoader } from "react-kakao-maps-sdk";

// Define the context type
interface MapContextType {
  mapInstance: kakao.maps.Map | null;
  setMapInstance: (map: kakao.maps.Map | null) => void;
  loading: boolean;
}

// Create the context with a default value
const MapContext = createContext<MapContextType | undefined>(undefined);

// Provider props type
interface MapProviderProps {
  children: ReactNode;
}

// Create the provider component
export const MapProvider: React.FC<MapProviderProps> = ({ children }) => {
  const [loading] = useKakaoLoader({
    appkey: import.meta.env.VITE_DEFAULT_KAKAO_MAP_API_KEY,
  });

  const [mapInstance, setMapInstance] = useState<kakao.maps.Map | null>(null);

  const value = {
    mapInstance,
    setMapInstance,
    loading,
  };

  return <MapContext.Provider value={value}>{children}</MapContext.Provider>;
};

// Custom hook for using the map context
export const useMap = (): MapContextType => {
  const context = useContext(MapContext);
  if (context === undefined) {
    throw new Error("useMap must be used within a MapProvider");
  }
  return context;
};

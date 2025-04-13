import { useState } from "react";
import KakaoMap from "../KakaoMap";
import { Marker } from "../Marker";
import { MapProvider } from "../contexts/MapContext";

const MarkerExample = () => {
  const [markers, setMarkers] = useState([
    {
      id: 1,
      position: { lat: 37.5665, lng: 126.978 }, // Seoul
      title: "서울",
      content: "서울특별시",
    },
    {
      id: 2,
      position: { lat: 35.1796, lng: 129.0756 }, // Busan
      title: "부산",
      content: "부산광역시",
    },
    {
      id: 3,
      position: { lat: 33.5097, lng: 126.5219 }, // Jeju
      title: "제주",
      content: "제주특별자치도",
    },
  ]);

  const [center, setCenter] = useState({ lat: 36.5, lng: 127.8 }); // Center of South Korea
  const [level, setLevel] = useState(13); // Zoom level for the entire country

  const handleMarkerClick = (marker: (typeof markers)[0]) => {
    setCenter(marker.position);
    setLevel(3);
    console.log(`Clicked on marker: ${marker.title}`);
  };

  return (
    <div style={{ width: "100%", height: "500px" }}>
      <MapProvider>
        <KakaoMap width="100%" height="100%" center={center} level={level}>
          {markers.map((marker) => (
            <Marker
              key={marker.id}
              position={marker.position}
              title={marker.title}
              content={marker.content}
              onClick={() => handleMarkerClick(marker)}
            />
          ))}
        </KakaoMap>
      </MapProvider>
    </div>
  );
};

export default MarkerExample;

import { useState } from "react";
import KakaoMap from "../KakaoMap";
import { TextMarker } from "../TextMarker";
import { MapProvider } from "../contexts/MapContext";

const TextMarkerExample = () => {
  const [markers] = useState([
    {
      id: 1,
      position: { lat: 37.5665, lng: 126.978 }, // Seoul
      title: "서울",
      text: "서울",
    },
    {
      id: 2,
      position: { lat: 35.1796, lng: 129.0756 }, // Busan
      title: "부산",
      text: "부산",
    },
    {
      id: 3,
      position: { lat: 33.5097, lng: 126.5219 }, // Jeju
      title: "제주",
      text: "제주",
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
            <TextMarker
              key={marker.id}
              position={marker.position}
              title={marker.title}
              text={marker.text}
              textStyle={{
                color: "#fff",
                backgroundColor: "rgba(0, 0, 0, 0.7)",
                padding: "5px 10px",
                borderRadius: "4px",
                fontSize: "14px",
                fontWeight: "bold",
              }}
              onClick={() => handleMarkerClick(marker)}
            />
          ))}
        </KakaoMap>
      </MapProvider>
    </div>
  );
};

export default TextMarkerExample;

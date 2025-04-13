import { useState } from "react";
import KakaoMap from "../KakaoMap";
import { TextMarker } from "../TextMarker";
import { MapProvider } from "../contexts/MapContext";

const TextMarkerStylesExample = () => {
  const [markers] = useState([
    {
      id: 1,
      position: { lat: 37.5665, lng: 126.978 }, // Seoul
      title: "서울",
      text: "서울",
      style: {
        color: "#fff",
        backgroundColor: "#1e88e5",
        padding: "5px 10px",
        borderRadius: "4px",
        fontSize: "14px",
        fontWeight: "bold",
      },
      yOffset: -0.8, // Text above the marker
    },
    {
      id: 2,
      position: { lat: 35.1796, lng: 129.0756 }, // Busan
      title: "부산",
      text: "부산",
      style: {
        color: "#fff",
        backgroundColor: "#e53935",
        padding: "5px 10px",
        borderRadius: "20px",
        fontSize: "14px",
        fontWeight: "bold",
      },
      yOffset: 0, // Text at the center of the marker
    },
    {
      id: 3,
      position: { lat: 33.5097, lng: 126.5219 }, // Jeju
      title: "제주",
      text: "제주",
      style: {
        color: "#000",
        backgroundColor: "#ffeb3b",
        padding: "5px 10px",
        borderRadius: "0px",
        fontSize: "14px",
        fontWeight: "bold",
        custom: "box-shadow: 2px 2px 5px rgba(0,0,0,0.3);",
      },
      yOffset: 0.8, // Text below the marker
    },
    {
      id: 4,
      position: { lat: 37.45, lng: 127.15 }, // Seongnam
      title: "성남",
      text: "성남시",
      style: {
        color: "#fff",
        backgroundColor: "rgba(76, 175, 80, 0.8)",
        padding: "8px 15px",
        borderRadius: "8px",
        fontSize: "16px",
        fontWeight: "500",
        custom: "border: 2px solid white;",
      },
      yOffset: -1, // Further above the marker
    },
    {
      id: 5,
      position: { lat: 36.35, lng: 127.38 }, // Daejeon
      title: "대전",
      text: "대전광역시",
      style: {
        color: "#fff",
        backgroundColor: "transparent",
        padding: "5px",
        fontSize: "16px",
        fontWeight: "bold",
        custom: "text-shadow: 0px 0px 5px black;",
      },
      yOffset: 1.2, // Further below the marker
    },
  ]);

  return (
    <div style={{ width: "100%", height: "500px" }}>
      <MapProvider>
        <KakaoMap
          width="100%"
          height="100%"
          center={{ lat: 36, lng: 127.5 }}
          level={12}
        >
          {markers.map((marker) => (
            <TextMarker
              key={marker.id}
              position={marker.position}
              title={marker.title}
              text={marker.text}
              textStyle={marker.style}
              textYOffset={marker.yOffset}
              onClick={() => console.log(`Clicked on ${marker.title}`)}
            />
          ))}
        </KakaoMap>
      </MapProvider>
    </div>
  );
};

export default TextMarkerStylesExample;

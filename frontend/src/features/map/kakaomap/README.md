# KakaoMap Components

A collection of React components for integrating Kakao Maps into your React application.

## Setup

Before using these components, you need to set up a Kakao Maps API key:

1. Go to the [Kakao Developers site](https://developers.kakao.com)
2. Create an account and register your application
3. Get a JavaScript API key
4. Add your domain to the list of allowed domains
5. Set your API key in the environment variable `VITE_DEFAULT_KAKAO_MAP_API_KEY`

## Components

### MapProvider

The `MapProvider` component provides a context for sharing the map instance across components. It should wrap all map-related components.

```tsx
import { MapProvider } from './contexts/MapContext';

<MapProvider>
  {/* Map components go here */}
</MapProvider>
```

### KakaoMap

The `KakaoMap` component renders a Kakao Map with the specified options.

```tsx
import KakaoMap from './KakaoMap';
import { MapProvider } from './contexts/MapContext';

<MapProvider>
  <KakaoMap 
    width="100%" 
    height="500px" 
    center={{ lat: 37.5665, lng: 126.978 }} // Seoul coordinates
    level={3} // Zoom level
  />
</MapProvider>
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `width` | string | "100%" | Width of the map container |
| `height` | string | "100%" | Height of the map container |
| `center` | { lat: number, lng: number } | { lat: 37.5665, lng: 126.978 } | Center coordinates of the map |
| `level` | number | 3 | Zoom level (1-14, where 1 is the closest) |
| `children` | ReactNode | - | Child components to render inside the map (e.g., Markers) |

### Marker

The `Marker` component renders a marker on the map at the specified position.

```tsx
import { Marker } from './Marker';

<Marker
  position={{ lat: 37.5665, lng: 126.978 }}
  title="Seoul"
  content="Seoul, South Korea"
  onClick={() => console.log('Marker clicked')}
/>
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `position` | { lat: number, lng: number } | - | Position of the marker |
| `image` | { src: string, size: { width: number, height: number }, options?: { offset?: { x: number, y: number }, alt?: string, clickable?: boolean } } | - | Custom marker image |
| `onClick` | () => void | - | Function to call when the marker is clicked |
| `draggable` | boolean | false | Whether the marker can be dragged |
| `onDragStart` | () => void | - | Function to call when the marker starts being dragged |
| `onDragEnd` | (position: { lat: number, lng: number }) => void | - | Function to call when the marker is dropped after dragging |
| `zIndex` | number | - | Z-index of the marker |
| `opacity` | number | - | Opacity of the marker (0-1) |
| `title` | string | - | Title of the marker (displayed on hover) |
| `content` | ReactNode | - | Content to display in an info window when the marker is clicked |

### CustomOverlay

The `CustomOverlay` component renders custom HTML content at a specified position on the map.

```tsx
import { CustomOverlay } from './CustomOverlay';

<CustomOverlay
  position={{ lat: 37.5665, lng: 126.978 }}
  content="<div style='padding: 5px; background-color: #fff; border-radius: 5px;'>Hello World!</div>"
  yAnchor={1.0}
  xAnchor={0.5}
  onClick={() => console.log('Overlay clicked')}
/>
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `position` | { lat: number, lng: number } | - | Position of the overlay |
| `content` | string | - | HTML content of the overlay |
| `zIndex` | number | - | Z-index of the overlay |
| `yAnchor` | number | 1.0 | Y-axis anchor point (0-1, where 0 is top, 1 is bottom) |
| `xAnchor` | number | 0.5 | X-axis anchor point (0-1, where 0 is left, 1 is right) |
| `onClick` | () => void | - | Function to call when the overlay is clicked |

### TextMarker

The `TextMarker` component renders a marker with a text label. It combines the `Marker` and `CustomOverlay` components.

```tsx
import { TextMarker } from './TextMarker';

<TextMarker
  position={{ lat: 37.5665, lng: 126.978 }}
  title="Seoul"
  text="Seoul"
  textStyle={{
    color: "#fff",
    backgroundColor: "#000",
    padding: "5px 10px",
    borderRadius: "4px"
  }}
  onClick={() => console.log('TextMarker clicked')}
/>
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `position` | { lat: number, lng: number } | - | Position of the marker |
| `text` | string | - | Text to display with the marker |
| `textStyle` | { color?: string, backgroundColor?: string, padding?: string, borderRadius?: string, fontSize?: string, fontWeight?: string, custom?: string } | - | Style options for the text |
| `textYOffset` | number | 0.5 | Y-axis offset of the text relative to the marker (negative values move text up) |
| ... | ... | ... | All other `Marker` props except `content` |

## Examples

### Basic Marker Example

```tsx
import { KakaoMap, Marker, MapProvider } from './';

const MapWithMarkers = () => {
  const positions = [
    { lat: 37.5665, lng: 126.978, title: "Seoul" },
    { lat: 35.1796, lng: 129.0756, title: "Busan" },
  ];

  return (
    <MapProvider>
      <KakaoMap width="100%" height="500px">
        {positions.map((pos, index) => (
          <Marker 
            key={index}
            position={pos}
            title={pos.title}
            onClick={() => console.log(`Clicked ${pos.title}`)}
          />
        ))}
      </KakaoMap>
    </MapProvider>
  );
};
```

### Custom Marker Image Example

```tsx
<Marker
  position={{ lat: 37.5665, lng: 126.978 }}
  title="Seoul"
  image={{
    src: "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png",
    size: { width: 24, height: 35 }
  }}
/>
```

### Draggable Marker Example

```tsx
const [position, setPosition] = useState({ lat: 37.5665, lng: 126.978 });

<Marker
  position={position}
  draggable={true}
  onDragEnd={(newPosition) => setPosition(newPosition)}
/>
```

### Text Marker Example

```tsx
<TextMarker
  position={{ lat: 37.5665, lng: 126.978 }}
  title="Seoul"
  text="Seoul"
  textStyle={{
    color: "#fff",
    backgroundColor: "rgba(0, 0, 0, 0.7)",
    padding: "5px 10px",
    borderRadius: "4px",
    fontSize: "14px"
  }}
  textYOffset={-0.5} // Position the text above the marker
/>
```

### Custom HTML Overlay Example

```tsx
<CustomOverlay
  position={{ lat: 37.5665, lng: 126.978 }}
  content={`
    <div style="
      padding: 10px; 
      background-color: #fff; 
      border-radius: 5px;
      box-shadow: 0 2px 6px rgba(0,0,0,0.3);
    ">
      <h3 style="margin: 0;">Seoul</h3>
      <p style="margin: 5px 0 0;">Capital of South Korea</p>
    </div>
  `}
  yAnchor={1.0}
  xAnchor={0.5}
/>
``` 
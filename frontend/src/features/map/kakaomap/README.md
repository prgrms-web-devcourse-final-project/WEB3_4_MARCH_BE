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

## Examples

### Basic Marker Example

```tsx
import KakaoMap from './KakaoMap';
import { Marker } from './Marker';
import { MapProvider } from './contexts/MapContext';

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
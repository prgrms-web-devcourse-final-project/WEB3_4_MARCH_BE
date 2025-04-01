declare namespace kakao {
  namespace maps {
    class KakaoMap {
      constructor(container: HTMLElement, options: MapOptions);
      setCenter(latlng: LatLng): void;
      setLevel(level: number): void;
      addControl(control: MapControl, position: ControlPosition): void;
    }

    class LatLng {
      constructor(lat: number, lng: number);
    }

    class ZoomControl implements MapControl {}
    class MapTypeControl implements MapControl {}

    interface MapOptions {
      center: LatLng;
      level: number;
    }

    interface MapControl {
      getElement?: () => HTMLElement;
      setPosition?: (position: ControlPosition) => void;
    }

    enum ControlPosition {
      TOP,
      TOPRIGHT,
      RIGHT,
      BOTTOM,
      BOTTOMRIGHT,
      BOTTOMLEFT,
      LEFT,
      TOPLEFT,
    }

    // Just alias KakaoMap as Map for compatibility
    type Map = KakaoMap;
  }
}

interface Window {
  kakao: typeof kakao;
}

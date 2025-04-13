import type { MarkerProps } from "./Marker";
import { Marker } from "./Marker";
import { CustomOverlay } from "./CustomOverlay";

export interface TextMarkerProps extends Omit<MarkerProps, "content"> {
  /**
   * Text to display above the marker
   */
  text: string;
  /**
   * Text style options
   */
  textStyle?: {
    /**
     * Text color
     */
    color?: string;
    /**
     * Text background color
     */
    backgroundColor?: string;
    /**
     * Text padding
     */
    padding?: string;
    /**
     * Text border radius
     */
    borderRadius?: string;
    /**
     * Text font size
     */
    fontSize?: string;
    /**
     * Text font weight
     */
    fontWeight?: string;
    /**
     * Custom CSS styles
     */
    custom?: string;
  };
  /**
   * Y-offset for text (negative values move text up)
   */
  textYOffset?: number;
}

export const TextMarker: React.FC<TextMarkerProps> = ({
  position,
  text,
  textStyle = {},
  textYOffset = 0.5,
  ...markerProps
}) => {
  const {
    color = "#fff",
    backgroundColor = "#000",
    padding = "4px 8px",
    borderRadius = "4px",
    fontSize = "12px",
    fontWeight = "bold",
    custom = "",
  } = textStyle;

  // Create HTML content for the text overlay
  const textContent = `
    <div style="
      color: ${color};
      background-color: ${backgroundColor};
      padding: ${padding};
      border-radius: ${borderRadius};
      font-size: ${fontSize};
      font-weight: ${fontWeight};
      text-align: center;
      white-space: nowrap;
      ${custom}
    ">
      ${text}
    </div>
  `;

  return (
    <>
      {/* Render the marker */}
      <Marker position={position} {...markerProps} />

      {/* Render the text overlay above the marker */}
      <CustomOverlay
        position={position}
        content={textContent}
        yAnchor={1.0 + textYOffset}
        xAnchor={0.5}
        zIndex={markerProps.zIndex ? markerProps.zIndex + 1 : undefined}
        onClick={markerProps.onClick}
      />
    </>
  );
};

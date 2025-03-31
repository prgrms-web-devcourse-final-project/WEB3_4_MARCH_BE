import { stackflow } from "@stackflow/react";
import { basicRendererPlugin } from "@stackflow/plugin-renderer-basic";
import { basicUIPlugin } from "@stackflow/plugin-basic-ui";

import ExploreActivity from "../activities/ExploreActivity";
import MapActivity from "../activities/MapActivity";
import ChatActivity from "../activities/ChatActivity";
import ProfileActivity from "../activities/ProfileActivity";

export const { Stack, useFlow } = stackflow({
  transitionDuration: 350,
  activities: {
    ExploreActivity,
    MapActivity,
    ChatActivity,
    ProfileActivity,
  },
  plugins: [
    basicRendererPlugin(),
    basicUIPlugin({
      theme: "cupertino",
      appBar: {
        height: "0",
        backgroundColor: "transparent",
        borderSize: "0",
      },
    }),
  ],
  initialActivity: () => "ExploreActivity",
});

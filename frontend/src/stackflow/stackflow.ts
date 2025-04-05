import { stackflow } from "@stackflow/react";
import { basicRendererPlugin } from "@stackflow/plugin-renderer-basic";
import { basicUIPlugin } from "@stackflow/plugin-basic-ui";
import { historySyncPlugin } from "@stackflow/plugin-history-sync";

import ExploreActivity from "../activities/ExploreActivity";
import MapActivity from "../activities/MapActivity";
import ChatActivity from "../activities/ChatActivity";
import MyProfileActivity from "../activities/MyProfileActivity";
import ProfileDetailActivity from "../activities/ProfileDetailActivity";
import { LoginActivity } from "../activities/LoginActivity";

export const { Stack, useFlow } = stackflow({
  transitionDuration: 350,
  activities: {
    LoginActivity,
    ExploreActivity,
    MapActivity,
    ChatActivity,
    MyProfileActivity,
    ProfileDetailActivity,
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
    historySyncPlugin({
      routes: {
        LoginActivity: "/login",
        ExploreActivity: "/explorer",
        MapActivity: "/map",
        ChatActivity: "/chat",
        MyProfileActivity: "/profile",
        ProfileDetailActivity: "/profile/:userId",
      },
      fallbackActivity: () => "LoginActivity",
    }),
  ],
  initialActivity: () => "LoginActivity",
});

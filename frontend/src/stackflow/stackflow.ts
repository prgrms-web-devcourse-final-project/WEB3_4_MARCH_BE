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
import { ProfileSetupActivity } from "../activities/ProfileSetupActivity";
import { LoginLoadingActivity } from "../activities/LoginLoadingActivity";

const activities = {
  LoginActivity,
  LoginLoadingActivity,
  ExploreActivity,
  MapActivity,
  ChatActivity,
  MyProfileActivity,
  ProfileDetailActivity,
  ProfileSetupActivity,
};

export const { Stack, useFlow } = stackflow({
  transitionDuration: 350,
  activities,
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
        LoginLoadingActivity: "/login-redirect",
        ExploreActivity: "/explorer",
        MapActivity: "/map",
        ChatActivity: "/chat",
        MyProfileActivity: "/profile",
        ProfileDetailActivity: "/profile/:userId",
        ProfileSetupActivity: "/profile-setup",
      },
      fallbackActivity: () => "ExploreActivity",
    }),
  ],
  initialActivity: () => "ExploreActivity",
});

export type ActivityName = keyof typeof activities;

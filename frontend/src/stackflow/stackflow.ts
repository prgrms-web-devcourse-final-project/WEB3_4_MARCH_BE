import { stackflow } from "@stackflow/react";
import { basicRendererPlugin } from "@stackflow/plugin-renderer-basic";
import { basicUIPlugin } from "@stackflow/plugin-basic-ui";
import TestActivity from "../Activities/TestActivity";

export const { Stack, useFlow } = stackflow({
  transitionDuration: 350,
  activities: {
    TestActivity,
  },
  plugins: [
    basicRendererPlugin(),
    basicUIPlugin({
      theme: "cupertino",
    }),
  ],
  initialActivity: () => "TestActivity",
});

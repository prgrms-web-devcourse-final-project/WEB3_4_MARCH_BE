import "reshaped/themes/reshaped/theme.css";
import "./index.css";
import "@stackflow/plugin-basic-ui/index.css";

import { Reshaped } from "reshaped";
import { Stack } from "./stackflow/stackflow";

const App = () => {
  return (
    <Reshaped theme="reshaped">
      <Stack />
    </Reshaped>
  );
};

export default App;

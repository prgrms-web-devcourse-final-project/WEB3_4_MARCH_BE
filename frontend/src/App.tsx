import "./global.css";
import "@stackflow/plugin-basic-ui/index.css";

import { Stack } from "./stackflow/stackflow";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const queryClient = new QueryClient();

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <Stack />
    </QueryClientProvider>
  );
};

export default App;

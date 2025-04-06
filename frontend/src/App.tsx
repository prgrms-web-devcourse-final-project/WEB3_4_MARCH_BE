import "./global.css";
import "@stackflow/plugin-basic-ui/index.css";

import { Stack } from "./stackflow/stackflow";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ErrorBoundary } from "react-error-boundary";
import AppScreenLayout from "./layout/AppScreenLayout";

const queryClient = new QueryClient();

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="bg-amber-100">
        <div className="relative h-[100vh] w-full max-w-md mx-auto overflow-hidden flex flex-col ">
          <Stack />
        </div>
      </div>
    </QueryClientProvider>
  );
};

export default App;

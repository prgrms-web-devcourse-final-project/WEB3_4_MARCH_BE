import ExploreView from "../features/explorer/ExplorerView";
import AppScreenLayout from "../layout/AppScreenLayout";

type ExploreActivityProps = {
  params: {};
};

const ExploreActivity: React.FC<ExploreActivityProps> = ({ params: {} }) => {
  return (
    <AppScreenLayout title="CONNECT TO">
      <ExploreView />
    </AppScreenLayout>
  );
};

export default ExploreActivity;

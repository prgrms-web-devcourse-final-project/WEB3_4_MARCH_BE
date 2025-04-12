import ExploreView from "../features/explorer/ExplorerView";
import AppScreenLayout from "../layout/AppScreenLayout";
import { useMatchings } from "../features/matching/useMatchings";
type ExploreActivityProps = {
  params: {};
};

const ExploreActivity: React.FC<ExploreActivityProps> = ({ params: {} }) => {
  const { data, isLoading, isError } = useMatchings();

  console.log(data);

  return (
    <AppScreenLayout title="CONNECT TO">
      <ExploreView />
    </AppScreenLayout>
  );
};

export default ExploreActivity;

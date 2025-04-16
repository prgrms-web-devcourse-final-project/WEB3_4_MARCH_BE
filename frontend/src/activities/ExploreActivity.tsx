import AppScreenLayout from "../layout/AppScreenLayout";
import { useMatchings } from "../features/matching/useMatchings";
import ExploreView from "../features/matching/ExplorerView";
import { DumyMatchings } from "../features/matching/dumy";
import { Loading } from "../components/Loading";

type ExploreActivityProps = {
  params: {};
};

const ExploreActivity: React.FC<ExploreActivityProps> = ({ params: {} }) => {
  const { data, isLoading, isError } = useMatchings();

  return (
    <AppScreenLayout title="CONNECT TO">
      {isLoading && (
        <div className="h-full w-full flex flex-col items-center justify-center">
          <Loading text="추천 데이터를 불러오고 있습니다..." />
        </div>
      )}
      {isError && <div>추천 데이터를 불러오는데 실패했습니다.</div>}
      {!isLoading && !isError && <ExploreView matchings={data?.data?.length ? data.data : DumyMatchings} />}
    </AppScreenLayout>
  );
};

export default ExploreActivity;


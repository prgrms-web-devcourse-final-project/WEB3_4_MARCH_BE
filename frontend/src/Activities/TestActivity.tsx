import { AppScreen } from "@stackflow/plugin-basic-ui";
import Demo from "../Test/Demo";

type TestAcitivityProps = {
  params: {
    title: string;
  };
};

const TestActivity: React.FC<TestAcitivityProps> = ({ params: { title } }) => {
  return (
    <AppScreen appBar={{ title }}>
      <Demo />
    </AppScreen>
  );
};

export default TestActivity;

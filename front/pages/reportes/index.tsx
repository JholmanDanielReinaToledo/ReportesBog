import { Col, Layout, Row, Spin } from "antd";
import BasicPage from "../../src/common/Components/BasicPage";
import Mapa from "../../src/common/Components/Mapa";

const ReportesPage = () => {
  return (
    <BasicPage>
      <Row>
        <Col span={18} push={6}>
          <Mapa />
        </Col>
        <Col span={6} pull={18}>
          asd
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPage;

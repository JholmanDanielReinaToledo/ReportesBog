import { useQuery } from "@apollo/client";
import { Badge, Col, Descriptions, Row } from "antd";
import moment from "moment";
import { NextPage } from "next";
import { useEffect, useState } from "react";
import BasicPage from "../../src/common/Components/BasicPage";
import Map from "../../src/common/Components/IndexMap";
import Mapa from "../../src/common/Components/Mapa";
import { GET_INCONVENIENTE_BY_ID } from "../../src/graphql/querys";
import { Inconveniente } from "../../src/types";
moment.locale('es');

const ReportesPageDetalle = () => {
  const [inconveniente, setInconveniente] = useState<Inconveniente>();
  const { loading, error, data } = useQuery(GET_INCONVENIENTE_BY_ID, {
    variables: {id: 1},
  });

  useEffect(() => {
    if (data) {
      if (data.inconvenienteById) {
        setInconveniente(data.inconvenienteById);
      }
    }
  }, [data]);

  return (
    <BasicPage>
      <Row>
        <Col span={12}>
          Información del reporte
          <Descriptions bordered>
            <Descriptions.Item label="Descripción">{inconveniente?.descripcion}</Descriptions.Item>
          </Descriptions>
          <Descriptions bordered>
            <Descriptions.Item label="Descripción">{moment(inconveniente?.fechaCreacion).format("MMMM Do YYYY")}</Descriptions.Item>
          </Descriptions>
        </Col>
        <Col span={12}>
          <Map />
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPageDetalle;

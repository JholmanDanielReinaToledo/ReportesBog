import { Card, Col, Layout, Row, Space, Spin } from "antd";
import { map, size } from "lodash";
import { useEffect, useState } from "react";
import { getAllInconvenientes } from "../../src/Apollo/functions";
import BasicPage from "../../src/common/Components/BasicPage";
import Mapa from "../../src/common/Components/Mapa";
import { Inconveniente } from "../../src/types";
import moment from 'moment'

const ReportesPage = () => {
  const [inconvenientes, setInconvenientes] = useState<Inconveniente[]>([]);

  console.log(inconvenientes);

  useEffect(() => {
    getAllInconvenientes().then(
      (inconvenients) => setInconvenientes(inconvenients.data.allInconvenientes.nodes)
    );
  }, []);

  return (
    <BasicPage>
      <Row>
        <Col span={18} push={6}>
          <Mapa />
        </Col>
        <Col span={6} pull={18}>
          <Space wrap>
            {
              size(inconvenientes) > 0 ? (
                <>
                  {
                    map(
                      inconvenientes,
                      (inconveniente) => {
                        console.log(inconveniente)
                        return (
                          <Card
                            title={`${moment(inconveniente.fechaCreacion).format('MMMM d, YYYY') }`}
                          >
                            <>
                              <p>{inconveniente?.direccionByIdDireccion ? 'Direccion' : 'Sin direccion'}</p>
                              <p>{inconveniente?.estadoReporteByIdEstado ? inconveniente?.estadoReporteByIdEstado.descripcion : 'Cargando'}</p>
                            </>
                          </Card>
                        )
                      }
                    )
                  }
                </>
              ) : (
                <>
                </>
              )
            }
          </Space>
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPage;

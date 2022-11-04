import { Button, Card, Col, Layout, Row, Space, Spin } from "antd";
import { map, size } from "lodash";
import { useEffect, useState } from "react";
import { getAllInconvenientes } from "../../src/Apollo/functions";
import BasicPage from "../../src/common/Components/BasicPage";
import Mapa from "../../src/common/Components/Mapa";
import { Inconveniente } from "../../src/types";
import moment from 'moment'
import { GET_ALL_INCONVENIENTES } from "../../src/graphql/querys";
import { useQuery } from '@apollo/client';
import { EyeFilled, EyeOutlined } from "@ant-design/icons";
import { useRouter } from "next/router";

const ReportesPage = () => {
  const [inconvenientes, setInconvenientes] = useState<Inconveniente[]>([]);
  const { loading, error, data } = useQuery(GET_ALL_INCONVENIENTES);

  const { push } = useRouter();

  console.log(data)

  useEffect(() => {
    if (data) {
      setInconvenientes(data.allInconvenientes.nodes)
    }
  }, [data])

  return (
    <BasicPage>
      <Row>
        <Col span={18} push={6}>
          <Mapa />
        </Col>
        <Col span={6} pull={18}>
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
                            style={{
                              maxWidth: '280px',
                            }}
                          >
                            <>
                              <p>{inconveniente?.direccionByIdDireccion ? 'Direccion' : 'Sin direccion'}</p>
                              <p>{inconveniente?.estadoReporteByIdEstado ? inconveniente?.estadoReporteByIdEstado.descripcion : 'Cargando'}</p>
                            </>
                            <Button
                              icon={<EyeOutlined />}
                              onClick={() => push(`reportes/${inconveniente.id}`)}
                            >
                              Detalles
                            </Button>
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
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPage;

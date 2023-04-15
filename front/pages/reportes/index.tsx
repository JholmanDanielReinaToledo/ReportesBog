import { Button, Card, Col, Layout, Pagination, Row, Space, Spin } from "antd";
import { map, size, slice } from "lodash";
import { useEffect, useState } from "react";
import BasicPage from "../../src/common/Components/BasicPage";
import Mapa from "../../src/common/Components/Mapa";
import { Inconveniente } from "../../src/types";
import moment from 'moment'
import { GET_ALL_INCONVENIENTES } from "../../src/graphql/querys";
import { useQuery } from '@apollo/client';
import { EyeFilled, EyeOutlined } from "@ant-design/icons";
import { useRouter } from "next/router";
import Map from "../../src/common/Components/IndexMap";

const ReportesPage = () => {
  const [inconvenientes, setInconvenientes] = useState<Inconveniente[]>([]);
  const [puntos, setPuntos] = useState<any[]>();
  const { loading, error, data } = useQuery(GET_ALL_INCONVENIENTES);

  const { push } = useRouter();

  const [pageSize, setPageSize] = useState<number>(3);
  const [pageNumber, setPageNumber] = useState<number>(1);

  const cantidadMostrada = ((pageNumber - 1) * pageSize);

  useEffect(() => {
    if (inconvenientes) {
      const puntosTemp: any[] = [];
      map(inconvenientes, (inconveniente) => {
        console.log(inconveniente?.direccionByIdDireccion?.localizacion)
        if (inconveniente?.direccionByIdDireccion?.localizacion) {
          // @ts-ignore
          puntosTemp.push({x: inconveniente?.direccionByIdDireccion?.localizacion.x, y: inconveniente?.direccionByIdDireccion?.localizacion.y})
          console.log(inconveniente.direccionByIdDireccion.localizacion);
        }
      });
      setPuntos(puntosTemp);
    }
  }, [inconvenientes]);

  useEffect(() => {
    if (data) {
      setInconvenientes(data.allInconvenientes.nodes)
    }
  }, [data])

  return (
    <BasicPage>
      <Row>
        <Col span={18} push={6}>
          {
            typeof window !== "undefined" && (
              <Map puntos={puntos}/>
            )
          }
        </Col>
        <Col span={6} pull={18} style={{}}>
          
            
            {
              size(inconvenientes) > 0 && (
                <>
                  {
                    map(
                      slice(inconvenientes, cantidadMostrada, cantidadMostrada + pageSize),
                      (inconveniente) => {
                        return (
                          <Card
                            title={`${moment(inconveniente.fechaCreacion).format('MMMM d, YYYY') }`}
                            style={{
                              maxWidth: '280px',
                              marginBottom: '20px',
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
                  {
                    size(inconvenientes) > 3 && (
                      <Row justify="center">
                        <Pagination
                          simple
                          pageSize={pageSize}
                          showSizeChanger
                          total={size(inconvenientes)}
                          onChange={(page, pS) => {
                            if (page !== pageNumber) {
                              setPageNumber(page);
                            }
                            if (pS !== pageSize) {
                              setPageSize(pS || 3);
                            }
                          }}
                        />
                      </Row>
                    )
                  }
                </>
              )
            }
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPage;

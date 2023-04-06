import { PoweroffOutlined } from "@ant-design/icons";
import { useMutation, useQuery } from "@apollo/client";
import { Badge, Button, Carousel, Col, Descriptions, Row } from "antd";
import { map } from "lodash";
import moment from "moment";
import { NextPage } from "next";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import BasicPage from "../../src/common/Components/BasicPage";
import Map from "../../src/common/Components/IndexMap";
import Mapa from "../../src/common/Components/Mapa";
import { UPDATE_REPORTE } from "../../src/graphql/mutations";
import {
  GET_ESTADO_REPORTE,
  GET_INCONVENIENTE_BY_ID,
} from "../../src/graphql/querys";
import { Inconveniente, Direccion } from "../../src/types";
moment.locale("es");

const ReportesPageDetalle = () => {
  const { query } = useRouter();

  const [idReporte, setIdReporte] = useState<number>();
  const [inconveniente, setInconveniente] = useState<Inconveniente>();
  const { data } = useQuery(GET_INCONVENIENTE_BY_ID, {
    variables: { id: idReporte },
  });

  console.log(inconveniente);

  const { data: estadosReporte } = useQuery(GET_ESTADO_REPORTE);
  const [updateUser, { loading }] = useMutation(UPDATE_REPORTE);

  useEffect(() => {
    if (data) {
      if (data.inconvenienteById) {
        setInconveniente(data.inconvenienteById);
      }
    }
  }, [data]);

  useEffect(() => {
    const { id } = query;
    const numId = Number(id);
    if (!isNaN(numId)) {
      setIdReporte(numId);
    }
  }, [query]);

  const updateEstado = async (id: number) => {
    await updateUser({
      variables: {
        data: {
          id: inconveniente?.id,
          inconvenientePatch: {
            id: inconveniente?.id,
            descripcion: inconveniente?.descripcion,
            idEstado: id,
            fechaCreacion: inconveniente?.fechaCreacion,
            idDireccion: inconveniente?.idDireccion,
            idUsuario: inconveniente?.idUsuario,
          },
        },
      },
    });
  };

  const direccionToString = (direccion: Direccion) => {
    return `${direccion.cruceDesde.descripcion} ${direccion.numeroDesde} ${direccion.letraDesde} ${direccion.orientacionDesde.descripcion} # ${direccion.numeroHasta} ${direccion.letraHasta} ${direccion.orientacionHasta.descripcion}`;
  };

  return (
    <BasicPage>
      <Row>
        <Col span={12}>
          <Descriptions bordered title='Información del reporte'>
            <Descriptions.Item label='Descripción' span={3}>
              {inconveniente?.descripcion}
            </Descriptions.Item>
            <Descriptions.Item label='Fecha' span={3}>
              {moment(inconveniente?.fechaCreacion).format("MMMM Do YYYY")}
            </Descriptions.Item>
            <Descriptions.Item label='Estado' span={3}>
              {inconveniente?.estadoReporteByIdEstado?.descripcion}
            </Descriptions.Item>
          </Descriptions>
          <br />
          {inconveniente?.idDireccion && (
            <>
              <Descriptions bordered title='Dirección'>
                <Descriptions.Item label='Localidad' span={3}>
                  {
                    inconveniente?.direccionByIdDireccion?.localidad
                      ?.descripcion
                  }
                </Descriptions.Item>
                <Descriptions.Item label='Barrio' span={3}>
                  {inconveniente?.direccionByIdDireccion?.barrio?.descripcion}
                </Descriptions.Item>
                <Descriptions.Item label='Dirección' span={3}>
                  {direccionToString(inconveniente?.direccionByIdDireccion)}
                </Descriptions.Item>
              </Descriptions>
            </>
          )}
          <br />
          <Button danger loading={loading} onClick={() => updateEstado(4)}>
            Rechazar
          </Button>
          <Button loading={loading} onClick={() => updateEstado(3)}>
            Revisado
          </Button>
          <p />
          <Carousel >

            {
              // @ts-ignore
            map(inconveniente?.fotosByIdInconveniente.nodes, (foto) => {
              return (
                <div>
                  <img src={foto.enlace} height={200} />
                </div>
              );
            })}
          </Carousel>
        </Col>

        <Col span={12}>
          {inconveniente?.direccionByIdDireccion?.localizacion ? (
            <Map
              puntos={[inconveniente?.direccionByIdDireccion?.localizacion]}
            />
          ) : (
            <Map puntos={[]} />
          )}
        </Col>
      </Row>
    </BasicPage>
  );
};

export default ReportesPageDetalle;

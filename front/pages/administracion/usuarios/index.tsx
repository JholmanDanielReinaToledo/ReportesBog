import { useQuery } from "@apollo/client";
import { Button, Card, Space } from "antd";
import { map } from "lodash";
import { useEffect, useState } from "react";
import BasicPage from "../../../src/common/Components/BasicPage";
import CardPropio from "../../../src/common/Components/CardPropio";
import { UsuarioLogin } from "../../../src/common/types";
import { GET_ALL_USUARIOS } from "../../../src/graphql/querys";

const IndexUsuariosPage = () => {
  const [usuario, setUsuario] = useState<UsuarioLogin>();
  const [usuarios, setUsuarios] = useState<UsuarioLogin[]>();

  const { loading, error, data } = useQuery(GET_ALL_USUARIOS); 

  useEffect(() => {
    if (data?.allUsuarios?.nodes) {
      setUsuarios(data?.allUsuarios?.nodes);
    }
  }, [data]);

  console.log(usuarios)

  return (
    <BasicPage>
      <Space wrap>
        {
          map(
            usuarios,
            (usuario) => {
              return (
                <CardPropio
                  titulo={`${usuario.nombre} `}
                  description={
                    <>
                      <p>{usuario.correoElectronico}</p>
                      <p>{usuario.identificacion}</p>
                      <p>{usuario?.grupoByIdGrupo?.nombre || ''}</p>
                    </>
                  }
                />
              )
            }
          )
        }
      </Space>
    </BasicPage>
  )
}

export default IndexUsuariosPage;

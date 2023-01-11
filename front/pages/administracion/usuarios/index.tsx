import { useMutation, useQuery } from "@apollo/client";
import { Button, Card, Space } from "antd";
import { map } from "lodash";
import { useEffect, useState } from "react";
import BasicPage from "../../../src/common/Components/BasicPage";
import CardPropio from "../../../src/common/Components/CardPropio";
import { UsuarioLogin } from "../../../src/common/types";
import { UPDATE_USER } from "../../../src/graphql/mutations";
import { GET_ALL_USUARIOS } from "../../../src/graphql/querys";

const IndexUsuariosPage = () => {
  const [usuario, setUsuario] = useState<UsuarioLogin>();
  const [usuarios, setUsuarios] = useState<UsuarioLogin[]>();

  const { loading, error, data, refetch } = useQuery(GET_ALL_USUARIOS); 
  const [updateUser] = useMutation(UPDATE_USER);


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
                  actions={{
                    primary: {
                      permission: !usuario.activo,
                      text: 'Activar',
                      function: async () => {
                        await updateUser({
                          variables: {
                            data: {
                              "id": usuario.id,
                              "nombre": usuario.nombre,
                              "apellido": usuario.apellido,
                              "idTipoDocumento": usuario.idTipoDocumento,
                              "identificacion": usuario.identificacion,
                              "correoElectronico": usuario.correoElectronico,
                              "password": usuario.password,
                              "activo": true,
                              "idGrupo": usuario.idGrupo
                            },
                            id: usuario.id,
                          }
                        });
                        refetch();
                        console.log(usuario);
                      }
                    }
                  }}
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

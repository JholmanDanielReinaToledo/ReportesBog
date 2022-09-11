import { gql } from "postgraphile";

export const INSERT_NEW_USER = gql`
mutation insertNewUser ($data: CreateUsuarioInput!) {
    createUsuario(input: $data) {
      usuario {
        id
        nombre
        apellido
        idTipoDocumento
        identificacion
        correoElectronico
        activo
        idGrupo
      }
    }
  }
`;

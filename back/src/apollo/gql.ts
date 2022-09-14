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

export const GET_USER_BY_IDENTIFICACION = gql`
query getUserByIdentificacion($identificacion: String!) {
  usuarioByIdentificacion (identificacion: $identificacion) {
    id
    identificacion
    correoElectronico
    password
    activo
  }
}
`;

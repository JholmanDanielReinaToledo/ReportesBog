import gql from "graphql-tag";

export const UPDATE_USER = gql`
mutation activateUser ($data: UsuarioPatch!, $id: BigInt!) {
  updateUsuarioById(input: {usuarioPatch: $data, id: $id}) {
    usuario {
      id
      nombre
    }
  }
}
`;

export const UPDATE_REPORTE = gql`
mutation updateReporte($data: UpdateInconvenienteByIdInput!) {
  updateInconvenienteById(input: $data) {
    clientMutationId
  }
}
`;
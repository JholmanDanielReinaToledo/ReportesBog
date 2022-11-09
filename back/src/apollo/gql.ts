import { gql } from "postgraphile";

export const INSERT_INCONVENIENTE = gql`
mutation insertInconveniente($datos: InconvenienteInput!) {
  createInconveniente (input: {inconveniente: $datos}) {
    inconveniente {
      id
      descripcion
      idEstado
      fechaCreacion
      idDireccion
      idUsuario
    }
  }
}`;

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


export const CREATE_NEW_REPORT = gql`
mutation insertNewReport ($data: CreateInconvenienteInput!) {
  createInconveniente(input:$data) {
    inconveniente {
      id
      idUsuario
      idEstado
      descripcion
      }
    }
  }
`;


export const GET_LOCALIDADES = gql`
query getAllLocalidades {
  allLocalidads(orderBy: DESCRIPCION_ASC) {
    Localidades:edges {
      localidad:node {
        id
        codigoLocalidad
        descripcion
      }
    }
  }
}
`;


export const GET_BARRIOS_BY_ID_LOCALIDAD = gql`
query getBarrioByLocalidad($idLocalidad:Int) {
  allBarrios(condition: {idLocalidad: $idLocalidad}, orderBy:DESCRIPCION_ASC) {
    Barrios:edges {
      barrio:node {
        id
        codigoBarrio
        descripcion
        localidad
        idLocalidad
      }
    }
  }
}
`;
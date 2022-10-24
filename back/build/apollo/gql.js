"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.GET_USER_BY_IDENTIFICACION = exports.INSERT_NEW_USER = exports.INSERT_INCONVENIENTE = void 0;
exports.GET_BARRIOS_BY_ID_LOCALIDAD = exports.GET_LOCALIDADES = exports.CREATE_NEW_REPORT = exports.GET_USER_BY_IDENTIFICACION = exports.INSERT_NEW_USER = void 0;
const postgraphile_1 = require("postgraphile");
exports.INSERT_INCONVENIENTE = (0, postgraphile_1.gql) `
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
exports.INSERT_NEW_USER = (0, postgraphile_1.gql) `
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
exports.GET_USER_BY_IDENTIFICACION = (0, postgraphile_1.gql) `
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
exports.CREATE_NEW_REPORT = (0, postgraphile_1.gql) `
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
exports.GET_LOCALIDADES = (0, postgraphile_1.gql) `
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
exports.GET_BARRIOS_BY_ID_LOCALIDAD = (0, postgraphile_1.gql) `
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

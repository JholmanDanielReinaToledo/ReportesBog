"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.GET_USER_BY_IDENTIFICACION = exports.INSERT_NEW_USER = exports.INSERT_INCONVENIENTE = void 0;
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

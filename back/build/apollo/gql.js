"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.INSERT_NEW_USER = void 0;
const postgraphile_1 = require("postgraphile");
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

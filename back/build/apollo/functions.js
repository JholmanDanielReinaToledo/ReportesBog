"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getBarrios = exports.getLocalidades = exports.createNewReport = exports.getUserByIdentificacion = exports.insertNewUser = void 0;
const gql_1 = require("./gql");
const utils_1 = require("./utils");
const insertNewUser = (user, res) => (0, utils_1.mutate)(gql_1.INSERT_NEW_USER, { data: {
        usuario: user
    } }, res);
exports.insertNewUser = insertNewUser;
const getUserByIdentificacion = (identificacion) => (0, utils_1.query)(gql_1.GET_USER_BY_IDENTIFICACION, {
    identificacion,
});
exports.getUserByIdentificacion = getUserByIdentificacion;
const createNewReport = (report, res) => (0, utils_1.mutate)(gql_1.CREATE_NEW_REPORT, {
    data: {
        inconveniente: report
    }
}, res);
exports.createNewReport = createNewReport;
const getLocalidades = () => (0, utils_1.query)(gql_1.GET_LOCALIDADES);
exports.getLocalidades = getLocalidades;
const getBarrios = (idLocalidad) => (0, utils_1.query)(gql_1.GET_BARRIOS_BY_ID_LOCALIDAD, {
    idLocalidad,
});
exports.getBarrios = getBarrios;

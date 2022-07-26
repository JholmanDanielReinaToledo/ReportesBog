"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.loginBodyReq = exports.validateNewUser = void 0;
const joi_1 = __importDefault(require("joi"));
exports.validateNewUser = joi_1.default.object({
    nombre: joi_1.default.string().required(),
    apellido: joi_1.default.string().required(),
    idTipoDocumento: joi_1.default.number().required(),
    identificacion: joi_1.default.string().required(),
    correoElectronico: joi_1.default.string().email().required(),
    password: joi_1.default.string().required().min(5),
});
exports.loginBodyReq = joi_1.default.object({
    identificacion: joi_1.default.string(),
    // correoElectronico: Joi.string().required().email(),
    password: joi_1.default.string().required().min(5),
});
// https://www.npmjs.com/package/schema-validator

import Joi from "joi";

export const validateNewUser = Joi.object({
  nombre: Joi.string().required(),
  apellido: Joi.string().required(),
  idTipoDocumento: Joi.number().required(),
  identificacion: Joi.string().required(),
  correoElectronico: Joi.string().email().required(),
  password: Joi.string().required().min(5),
});

export const loginBodyReq = Joi.object({
  identificacion: Joi.string(),
  // correoElectronico: Joi.string().required().email(),
  password: Joi.string().required().min(5),
});

// https://www.npmjs.com/package/schema-validator

import Joi from "joi";

export const validateNewUser = Joi.object({
  nombre: Joi.string().required(),
  apellido: Joi.string().required(),
  id_tipo_documento: Joi.number().required(),
  identificacion: Joi.string().required(),
  correo_electronico: Joi.string().email().required(),
  password: Joi.string().required(),
});

export const loginBodyReq = Joi.object({
  correo_electronico: Joi.string().required().email(),
  password: Joi.string().required().min(5),
});

// https://www.npmjs.com/package/schema-validator

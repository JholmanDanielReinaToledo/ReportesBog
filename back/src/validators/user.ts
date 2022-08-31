import Joi from "joi";

export const validateNewUser = {
  nombre: Joi.string().required(),
  apellido: Joi.string().required(),
  id_tipo_documento: Joi.number().required(),
  identificacion: Joi.string().required(),
  correo_electronico: Joi.string().email().required(),
  password: Joi.string().required(),
};

// https://www.npmjs.com/package/schema-validator

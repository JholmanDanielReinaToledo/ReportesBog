import express from 'express'
//const Joi = require("joi"); 
import * as Joi from "joi";
//import jwt from "jsonwebtoken";

// import { clientPG } from '../database/connection'

const router = express.Router()

require('dotenv').config()



const loginBodyReq = Joi.object({
  usuario: Joi.string().required(),
  password: Joi.string().required()
})


router.post('/login', async (req, res) => {

  const { error } = await loginBodyReq.validate(req.body);
  console.log(req.body);
  
  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details[0].message });
  }


  res.status(200).send(loginBodyReq)
})


router.post('/', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
})

router.post('/asd', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
})


/*
router.post('/register', (req, res) => {
  const {
    nombre,
    apellido,
    id_tipo_documento,
    identificacion,
    correo_electronico,
    password,
  } = req.body;
  



  console.log(req.body)
  res.status(200).send('cumple')
})
*/
export default router

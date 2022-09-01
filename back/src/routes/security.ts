import express from 'express'
//const Joi = require("joi"); 
import * as Joi from "joi";
import { loginBodyReq } from '../validators/user';
//import jwt from "jsonwebtoken";

// import { clientPG } from '../database/connection'

const router = express.Router()

router.post('/login', (req, res) => {

  const { error } = loginBodyReq.validate(req.body);
  console.log(req.body);

  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details });
  }


  res.status(200).send(req.body)
})


router.post('/', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
})

router.post('/asd', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
})

router.post('/register', (req, res) => {
  const { error } = loginBodyReq.validate(req.body);

  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details });
  }

  

  return res.status(200).send('created')
})

export default router

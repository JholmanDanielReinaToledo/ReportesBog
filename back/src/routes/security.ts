import express from 'express'
// import { clientPG } from '../database/connection'

const router = express.Router()

require('dotenv').config()

router.post('/login', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
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

export default router

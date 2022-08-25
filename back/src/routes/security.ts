import express from 'express'
import { clientPG } from '../database/connection'

const router = express.Router()

require('dotenv').config()

clientPG.connect((err) => {
  if (err) {
    console.log(err)
  } else {
    console.log('Data loggin initiated')
  }
})

router.get('/login', (req, res) => {
  console.log(req.body)
  res.status(200).send('cumple')
})

export default router

import express from 'express'
import { loginBodyReq, validateNewUser } from '../validators/user';
import { hash } from 'bcrypt';
import { insertNewUser } from '../apollo/functions';
//import jwt from "jsonwebtoken";

const saltRounds = 10;
const myPlaintextPassword = 's0/\/\P4$$w0rD';
const someOtherPlaintextPassword = 'not_bacon';

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
  const { error } = validateNewUser.validate(req.body);

  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details });
  }

  hash(req.body.password, saltRounds, function(err, hash) {
    console.log(err, hash);
    if (hash) {
      insertNewUser({
        ...req.body,
        password: hash,
      }, res).then(console.log).catch(console.log)
      return res.status(200).send()
    }
  });
});

export default router

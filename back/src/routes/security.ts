import express from 'express'
import { loginBodyReq, validateNewUser } from '../validators/user';
import { hash } from 'bcrypt';
import { insertNewUser } from '../apollo/functions';
import jwt from "jsonwebtoken";
import bcrypt from "bcrypt";
import { encrypt } from '../utilities/encryption';

const saltRounds = 10;
const myPlaintextPassword = 's0/\/\P4$$w0rD';
const someOtherPlaintextPassword = 'not_bacon';

const router = express.Router()

const mockUserEmail = "jguerrap1@ucentral.edu.co";
const mockUserPassword = "$2b$10$a1/W3LrlRpyxt4OuIbkbN.wSu4W45y9cNIy6J2S5c/XYaHIrSy5Ca";

router.post('/login', async (req, res) => {

  const { error } = loginBodyReq.validate(req.body);

  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details });
  }

  const body = req.body;

  const password = body.password;
  const correo_electronico = body.correo_electronico;

  /*
  const userPasswordEncrypt = await bcrypt.hash(password, saltRounds);

  console.log(mockUserPassword);
  console.log(userPasswordEncrypt);

  const isPasswordValid = await bcrypt.compare(userPasswordEncrypt, mockUserPassword);
  console.log(isPasswordValid);
  

  if(!isPasswordValid) return res.status(200).send("Wrong Password");
*/

  const passwordEnc = encrypt(password);

  console.log(passwordEnc);
  
  //const passwordDec = encrypt(passwordEnc.encryptedData,passwordEnc.iv);

  //console.log(passwordDec);

  res.status(200).send(body);
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
      return insertNewUser({
        ...req.body,
        password: hash,
      }, res).then(
        (respo) => console.log(respo)
      ).catch(
        (respo) => console.log(respo)
      )
    }
    return res.status(400).send()
  });
});

export default router

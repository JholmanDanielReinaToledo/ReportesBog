import express from 'express'
import { loginBodyReq, validateNewUser } from '../validators/user';
import { hash, compare } from 'bcrypt';
import { getUserByIdentificacion, insertNewUser } from '../apollo/functions';
// import { sendMessage } from '../mail/mailer';
import { sign } from 'jsonwebtoken';
import moment from 'moment';

require('dotenv').config()

const saltRounds = 10;
const router = express.Router()

router.post('/login', async (req, res) => {

  const { error } = loginBodyReq.validate(req.body);

  if (error) {
    console.log("error")
    return res.status(400).json({ error: error.details });
  }

  const { password, identificacion } = req.body;

  return getUserByIdentificacion(identificacion).then(
    (re) => {
      const { data } = re;
      const { usuarioByIdentificacion } = data;
      return compare(password, usuarioByIdentificacion.password, function (err, result) {
        if (result && usuarioByIdentificacion.activo) {
          // arma el jwt y lo devuelve
          if (process.env.TOKEN_SECRET) {
            const date = moment().add(40, 'minute').calendar();
            console.log(date)
            const token = sign({
              name: usuarioByIdentificacion.identificacion,
              id: usuarioByIdentificacion.id,
              correoElectronico: usuarioByIdentificacion.correoElectronico,
              expirationDate: date,
            }, process.env.TOKEN_SECRET)
            console.log(token)
            return res.status(200).send(JSON.stringify({token: token}))
          } else {
            return res.status(500).send(JSON.stringify({error:'Error 00F1'}))
          }
        } else if (!usuarioByIdentificacion.activo){
          return res.status(403).send(JSON.stringify({error:'El usuario no esta activo'}))
        } else {
          return res.status(403).send(JSON.stringify({error :'No se encontro un usuario con esa identificación y contraseña'}))
        }
      });
      // return res.status(200).send()
    }
  ).catch((err) => res.status(500).send(JSON.stringify({error:"Usuario no encontrado"})))
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
        () => {
          // sendMessage(req.body.correoElectronico);
        }
      ).catch(
        (respo) => console.log(respo)
      )
    }
    return res.status(400).send()
  });
});

export default router

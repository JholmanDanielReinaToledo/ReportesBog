import express from 'express'
import { loginBodyReq, validateNewUser } from '../validators/user';
import { hash, compare } from 'bcrypt';
import { createNewReport, getBarrios, getLocalidades, getUserByIdentificacion, insertNewUser } from '../apollo/functions';
// import { sendMessage } from '../mail/mailer';
import { decode, sign, verify } from 'jsonwebtoken';
import moment from 'moment';
import { map } from 'lodash';
import { number } from 'joi';
import { sendMessage } from '../mail/mailer';

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
            const token = sign({
              identificacion: usuarioByIdentificacion.identificacion,
              id: usuarioByIdentificacion.id,
              correoElectronico: usuarioByIdentificacion.correoElectronico,
              expirationDate: date,
            }, process.env.TOKEN_SECRET)
            console.log(token)
            return res.status(200).send(JSON.stringify({
              ...usuarioByIdentificacion,
              token: token,
            }))
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
          sendMessage(req.body.correoElectronico);
        }
      ).catch(
        (respo) => console.log(respo)
      )
    }
    return res.status(400).send()
  });
});





router.post('/create-report', async (req, res)=>{
  
  const token = req.header('authtoken');

  if (!token) return res.status(401).json({ error: 'Acceso denegado' })

  let secretKey = process.env.TOKEN_SECRET;
  
  if(secretKey === null || secretKey === undefined){
    return res.status(401).json({ error: 'Acceso denegado' })
  }

  console.log(token);
  
  const tokenValue1 = token.replace('"','')
  const tokenValue = tokenValue1.replace('"','')
  console.log(tokenValue);

  let userId = null;
  try{
    verify(tokenValue,secretKey, function(err:any, decoded:any) {
      
      if (err)return res.status(500).send({ auth: false, message: err }); 
      
      console.log(decoded)
      userId = decoded.id

    });

  }catch(error){
    return res.status(500).send({ auth: false, message: error }); 
  }
  
  return res.status(200).send(JSON.stringify({data:"Usuario encontrado "+userId}))
});



router.post('/add-report-direction', async (req, res)=>{
  const token = req.header('authtoken');

  if (!token) return res.status(401).json({ error: 'Acceso denegado' })

  let secretKey = process.env.TOKEN_SECRET;
  
  if(secretKey === null || secretKey === undefined){
    return res.status(401).json({ error: 'Acceso denegado' })
  }

  console.log(token);
  
  const tokenValue1 = token.replace('"','')
  const tokenValue = tokenValue1.replace('"','')
  console.log(tokenValue);

  let userId = null;
  try{
    verify(tokenValue,secretKey, function(err:any, decoded:any) {
      
      if (err)return res.status(500).send({ auth: false, message: err }); 
      
      console.log(decoded)
      userId = decoded.id

    });

  }catch(error){
    return res.status(500).send({ auth: false, message: error }); 
  }
  
  return res.status(200).send(JSON.stringify({data:"Usuario encontrado "+userId}))
});



router.get('/my-reports', (req, res)=>{

    const token = req.header('authtoken');

    if (!token) return res.status(401).json({ error: 'Acceso denegado' })

    let secretKey = process.env.TOKEN_SECRET;
    
    if(secretKey === null || secretKey === undefined){
      return res.status(401).json({ error: 'Acceso denegado' })
    }

    console.log(token);
    
    const tokenValue1 = token.replace('"','')
    const tokenValue = tokenValue1.replace('"','')
    console.log(tokenValue);

    let userId = null;
    let dataToSend
    try{
      verify(tokenValue,secretKey, function(err:any, decoded:any) {
        
        if (err)return res.status(500).send({ auth: false, message: err }); 
        
        console.log(decoded)
        userId = decoded.id
        dataToSend=decoded

      });

    }catch(error){
      return res.status(500).send({ auth: false, message: error }); 
    }

    return res.status(200).send(JSON.stringify({data:dataToSend}))
});

router.get('/localidades', (req, res)=>{
  return getLocalidades()
  .then((re)=> {

    let dataToResponse:Array<Object> = [];

    re.data.allLocalidads.Localidades.forEach((e:any)=>dataToResponse.push(e.localidad))

    res.send(dataToResponse);
  })
  .catch(() => res.status(500).send(JSON.stringify({error:"Ocurrio un problema"})))
});



router.post('/barrios', (req, res)=>{

  const idLocalidad:number = req.body.idLocalidad;

  console.log("localidad "+ idLocalidad);
  

  if(idLocalidad==null) return res.status(401).send(JSON.stringify({error:'No se envio un id de localidad'}))

  return getBarrios(idLocalidad)
  .then((re)=> {

    let dataToResponse:Array<Object> = [];

    re.data.allBarrios.Barrios.forEach((e:any)=>dataToResponse.push(e.barrio))

    res.send(dataToResponse);
  })
  .catch((err) => {return res.status(500).send(JSON.stringify({error:"Ourrio un problema"}))})
});


export default router

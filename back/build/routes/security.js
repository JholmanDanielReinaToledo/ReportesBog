"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const user_1 = require("../validators/user");
const bcrypt_1 = require("bcrypt");
const functions_1 = require("../apollo/functions");
// import { sendMessage } from '../mail/mailer';
const jsonwebtoken_1 = require("jsonwebtoken");
const moment_1 = __importDefault(require("moment"));
//import { sendMessage } from '../mail/mailer';
require('dotenv').config();
const saltRounds = 10;
const router = express_1.default.Router();
router.post('/login', (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const { error } = user_1.loginBodyReq.validate(req.body);
    if (error) {
        console.log("error");
        return res.status(400).json({ error: error.details });
    }
    const { password, identificacion } = req.body;
    return (0, functions_1.getUserByIdentificacion)(identificacion).then((re) => {
        const { data } = re;
        const { usuarioByIdentificacion } = data;
        return (0, bcrypt_1.compare)(password, usuarioByIdentificacion.password, function (err, result) {
            if (result && usuarioByIdentificacion.activo) {
                // arma el jwt y lo devuelve
                if (process.env.TOKEN_SECRET) {
                    const date = (0, moment_1.default)().add(40, 'minute').calendar();
                    const token = (0, jsonwebtoken_1.sign)({
                        identificacion: usuarioByIdentificacion.identificacion,
                        id: usuarioByIdentificacion.id,
                        correoElectronico: usuarioByIdentificacion.correoElectronico,
                        expirationDate: date,
                    }, process.env.TOKEN_SECRET);
                    console.log(token);
                    return res.status(200).send(JSON.stringify(Object.assign(Object.assign({}, usuarioByIdentificacion), { token: token })));
                }
                else {
                    return res.status(500).send(JSON.stringify({ error: 'Error 00F1' }));
                }
            }
            else if (!usuarioByIdentificacion.activo) {
                return res.status(403).send(JSON.stringify({ error: 'El usuario no esta activo' }));
            }
            else {
                return res.status(403).send(JSON.stringify({ error: 'No se encontro un usuario con esa identificación y contraseña' }));
            }
        });
        // return res.status(200).send()
    }).catch((err) => res.status(500).send(JSON.stringify({ error: "Usuario no encontrado" })));
}));
router.post('/register', (req, res) => {
    const { error } = user_1.validateNewUser.validate(req.body);
    if (error) {
        console.log("error");
        return res.status(400).json({ error: error.details });
    }
    (0, bcrypt_1.hash)(req.body.password, saltRounds, function (err, hash) {
        console.log(err, hash);
        if (hash) {
            return (0, functions_1.insertNewUser)(Object.assign(Object.assign({}, req.body), { password: hash }), res).then(() => {
                //sendMessage(req.body.correoElectronico);
            }).catch((respo) => console.log(respo));
        }
        return res.status(400).send();
    });
});
router.post('/create-report', (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const token = req.header('authtoken');
    if (!token)
        return res.status(401).json({ error: 'Acceso denegado' });
    let secretKey = process.env.TOKEN_SECRET;
    if (secretKey === null || secretKey === undefined) {
        return res.status(401).json({ error: 'Acceso denegado' });
    }
    console.log(token);
    const tokenValue1 = token.replace('"', '');
    const tokenValue = tokenValue1.replace('"', '');
    console.log(tokenValue);
    let userId = null;
    try {
        (0, jsonwebtoken_1.verify)(tokenValue, secretKey, function (err, decoded) {
            if (err)
                return res.status(500).send({ auth: false, message: err });
            console.log(decoded);
            userId = decoded.id;
        });
    }
    catch (error) {
        return res.status(500).send({ auth: false, message: error });
    }
    return res.status(200).send(JSON.stringify({ data: "Usuario encontrado " + userId }));
}));
const multer = require('multer');
/*
var multerS3 = require('multer-s3');

var aws = require('aws-sdk');
var s3 = new aws.S3();
var upload = multer({
  storage: multerS3({
      s3: s3,
      bucket: process.env.BUCKET_REPORT_IMAGES,
      key: function (req:Request, file:any, cb:any) {
          console.log(file);
          cb(null, randomUUID); //use Date.now() for unique file keys
      }
  })
});*/
const { S3Client } = require('@aws-sdk/client-s3');
const multerS3 = require('multer-s3');
const app = (0, express_1.default)();
const s3 = new S3Client();
const upload = multer({
    storage: multerS3({
        s3: s3,
        bucket: process.env.BUCKET_REPORT_IMAGES,
        acl: "public-read",
        metadata: function (req, file, cb) {
            console.log(file);
            const nombreCortado = file.originalname.split('.');
            const extension = nombreCortado[nombreCortado.length - 1];
            cb(null, { fieldName: file.fieldname, contentType: "application/" + extension });
        },
        key: function (req, file, cb) {
            const nombreCortado = file.originalname.split('.');
            const extension = nombreCortado[nombreCortado.length - 1];
            cb(null, Date.now().toString() + "." + extension);
        }
    })
});
//const storage = multer.memoryStorage()
//const upload = multer({ storage: storage })
//const upload = multer({ dest: './uploads/' })
router.post('/add-report-image', upload.array('file', 1), function (req, res, next) {
    // req['file'] is the `avatar` file
    // req['body'] will hold the text fields, if there were any  
    //res.send("")
    //req.files.location
    console.log(req.files);
    res.send(req.files);
});
/*
function a(req:express.Request, res:express.Response){

  //imga(req, res)
  console.log(req.files);
  
  res.json();
};
*/
function imga(req, res) {
    /*const token = req.header('authtoken');
  
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
  
  
  */
    //return res.send("ok");
    //return res.status(200).send(JSON.stringify({data:"Usuario encontrado "+userId}))
}
;
router.get('/my-reports', (req, res) => {
    const token = req.header('authtoken');
    if (!token)
        return res.status(401).json({ error: 'Acceso denegado' });
    let secretKey = process.env.TOKEN_SECRET;
    if (secretKey === null || secretKey === undefined) {
        return res.status(401).json({ error: 'Acceso denegado' });
    }
    console.log(token);
    const tokenValue1 = token.replace('"', '');
    const tokenValue = tokenValue1.replace('"', '');
    console.log(tokenValue);
    let userId = null;
    let dataToSend;
    try {
        (0, jsonwebtoken_1.verify)(tokenValue, secretKey, function (err, decoded) {
            if (err)
                return res.status(500).send({ auth: false, message: err });
            console.log(decoded);
            userId = decoded.id;
            dataToSend = decoded;
        });
    }
    catch (error) {
        return res.status(500).send({ auth: false, message: error });
    }
    return res.status(200).send(JSON.stringify({ data: dataToSend }));
});
router.get('/localidades', (req, res) => {
    return (0, functions_1.getLocalidades)()
        .then((re) => {
        let dataToResponse = [];
        re.data.allLocalidads.Localidades.forEach((e) => dataToResponse.push(e.localidad));
        res.send(dataToResponse);
    })
        .catch(() => res.status(500).send(JSON.stringify({ error: "Ocurrio un problema" })));
});
router.post('/barrios', (req, res) => {
    const idLocalidad = req.body.idLocalidad;
    console.log("localidad " + idLocalidad);
    if (idLocalidad == null)
        return res.status(401).send(JSON.stringify({ error: 'No se envio un id de localidad' }));
    return (0, functions_1.getBarrios)(idLocalidad)
        .then((re) => {
        let dataToResponse = [];
        re.data.allBarrios.Barrios.forEach((e) => dataToResponse.push(e.barrio));
        res.send(dataToResponse);
    })
        .catch((err) => { return res.status(500).send(JSON.stringify({ error: "Ourrio un problema" })); });
});
exports.default = router;

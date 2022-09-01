"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const user_1 = require("../validators/user");
//import jwt from "jsonwebtoken";
// import { clientPG } from '../database/connection'
const router = express_1.default.Router();
router.post('/login', (req, res) => {
    const { error } = user_1.loginBodyReq.validate(req.body);
    console.log(req.body);
    if (error) {
        console.log("error");
        return res.status(400).json({ error: error.details });
    }
    res.status(200).send(req.body);
});
router.post('/', (req, res) => {
    console.log(req.body);
    res.status(200).send('cumple');
});
router.post('/asd', (req, res) => {
    console.log(req.body);
    res.status(200).send('cumple');
});
router.post('/register', (req, res) => {
    const { nombre, apellido, id_tipo_documento, identificacion, correo_electronico, password, } = req.body;
    console.log(nombre, apellido, id_tipo_documento, identificacion, correo_electronico, password);
    console.log(req.body);
    res.status(200).send('cumple');
});
exports.default = router;

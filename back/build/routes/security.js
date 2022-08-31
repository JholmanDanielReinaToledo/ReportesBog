"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
// import { clientPG } from '../database/connection'
const router = express_1.default.Router();
router.post('/login', (req, res) => {
    console.log(req.body);
    res.status(200).send('cumple');
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

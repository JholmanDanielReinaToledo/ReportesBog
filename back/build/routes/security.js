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
const mailer_1 = require("../mail/mailer");
const jsonwebtoken_1 = require("jsonwebtoken");
const moment_1 = __importDefault(require("moment"));
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
                    console.log(date);
                    const token = (0, jsonwebtoken_1.sign)({
                        name: usuarioByIdentificacion.identificacion,
                        id: usuarioByIdentificacion.id,
                        correoElectronico: usuarioByIdentificacion.correoElectronico,
                        expirationDate: date,
                    }, process.env.TOKEN_SECRET);
                    return res.status(200).json(Object.assign(Object.assign({}, usuarioByIdentificacion), { token }));
                }
                else {
                    return res.status(500).send('Error 00F1');
                }
            }
            else if (!usuarioByIdentificacion.activo) {
                return res.status(403).send('El usuario no esta activo');
            }
            else {
                return res.status(403).send('No se encontro un usuario con esa identificación');
            }
        });
        // return res.status(200).send()
    }).catch((err) => res.status(500).send(err));
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
                (0, mailer_1.sendMessage)(req.body.correoElectronico);
            }).catch((respo) => console.log(respo));
        }
        return res.status(400).send();
    });
});
exports.default = router;

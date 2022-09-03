"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const user_1 = require("../validators/user");
const bcrypt_1 = require("bcrypt");
const functions_1 = require("../apollo/functions");
const saltRounds = 10;
const myPlaintextPassword = 's0/\/\P4$$w0rD';
const someOtherPlaintextPassword = 'not_bacon';
const router = express_1.default.Router();
router.post('/login', (req, res) => {
    const { error } = user_1.loginBodyReq.validate(req.body);
    if (error) {
        console.log("error");
        return res.status(400).json({ error: error.details });
    }
    const body = req.body;
    const password = body.password;
    const correo_electronico = body;
    res.status(200).send(body);
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
    const { error } = user_1.validateNewUser.validate(req.body);
    if (error) {
        console.log("error");
        return res.status(400).json({ error: error.details });
    }
    (0, bcrypt_1.hash)(req.body.password, saltRounds, function (err, hash) {
        console.log(err, hash);
        if (hash) {
            (0, functions_1.insertNewUser)(Object.assign(Object.assign({}, req.body), { password: hash }), res).then(console.log).catch(console.log);
            return res.status(200).send();
        }
    });
});
exports.default = router;

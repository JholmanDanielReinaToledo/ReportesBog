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
const encryption_1 = require("../utilities/encryption");
const saltRounds = 10;
const myPlaintextPassword = 's0/\/\P4$$w0rD';
const someOtherPlaintextPassword = 'not_bacon';
const router = express_1.default.Router();
const mockUserEmail = "jguerrap1@ucentral.edu.co";
const mockUserPassword = "$2b$10$a1/W3LrlRpyxt4OuIbkbN.wSu4W45y9cNIy6J2S5c/XYaHIrSy5Ca";
router.post('/login', (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    const { error } = user_1.loginBodyReq.validate(req.body);
    if (error) {
        console.log("error");
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
    const passwordEnc = (0, encryption_1.encrypt)(password);
    console.log(passwordEnc);
    //const passwordDec = encrypt(passwordEnc.encryptedData,passwordEnc.iv);
    //console.log(passwordDec);
    res.status(200).send(body);
}));
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
            return (0, functions_1.insertNewUser)(Object.assign(Object.assign({}, req.body), { password: hash }), res).then((respo) => console.log(respo)).catch((respo) => console.log(respo));
        }
        return res.status(400).send();
    });
});
exports.default = router;

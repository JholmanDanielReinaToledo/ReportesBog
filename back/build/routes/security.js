"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const connection_1 = require("../database/connection");
const router = express_1.default.Router();
require('dotenv').config();
connection_1.clientPG.connect((err) => {
    if (err) {
        console.log(err);
    }
    else {
        console.log('Data loggin initiated');
    }
});
router.get('/login', (req, res) => {
    console.log(req.body);
    res.status(200).send('cumple');
});
exports.default = router;

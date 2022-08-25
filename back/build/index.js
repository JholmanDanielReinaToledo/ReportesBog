"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const postgraphile_1 = require("postgraphile");
require('dotenv').config();
const app = (0, express_1.default)();
console.log(process.env.DB_URL, 'postgres://postgres:juanesloco123@localhost:5434/postgres');
const options = {
    origin: '*',
};
app.use((0, cors_1.default)(options));
app.use('/back', (0, postgraphile_1.postgraphile)(process.env.DB_URL || 'postgres://postgres:juanesloco123@localhost:5434/postgres', [
    'administracion',
    'aplicacion',
    'dominios',
    'public'
], {
    watchPg: true,
    graphiql: true,
    enhanceGraphiql: true,
}));
app.listen(process.env.PORT, () => {
    console.log('Server running in port ' + process.env.PORT);
});

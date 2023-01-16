"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const postgraphile_1 = require("postgraphile");
const postgis_1 = __importDefault(require("@graphile/postgis"));
const security_1 = __importDefault(require("./routes/security"));
const body_parser_1 = __importDefault(require("body-parser"));
const pg_1 = require("pg");
require('dotenv').config();
const app = (0, express_1.default)();
const options = {
    origin: '*',
};
/*
var fileupload = require("express-fileupload");
app.use(fileupload());
*/
const pool = new pg_1.Pool({
    host: process.env.HOST_DB,
    user: process.env.USER_DB,
    port: 5432,
    password: process.env.PASS_DB,
    max: 20,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 2000,
});
app.use(body_parser_1.default.json());
app.use((0, cors_1.default)(options));
app.use(security_1.default);
app.use('/back', (0, postgraphile_1.postgraphile)(pool, [
    'administracion',
    'aplicacion',
    'dominios',
    'public'
], {
    watchPg: true,
    graphiql: true,
    enhanceGraphiql: true,
    appendPlugins: [postgis_1.default],
}));
app.listen(process.env.PORT, () => {
    console.log('Server running in port ' + process.env.PORT);
});

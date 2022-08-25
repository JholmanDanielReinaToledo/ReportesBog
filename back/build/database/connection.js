"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.clientPG = void 0;
const pg_1 = require("pg");
exports.clientPG = new pg_1.Client(process.env.CONNECTION_STRING);

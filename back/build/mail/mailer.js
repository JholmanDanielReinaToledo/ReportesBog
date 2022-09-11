"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.sendMessage = void 0;
const emailjs_1 = require("emailjs");
require('dotenv').config();
const client = new emailjs_1.SMTPClient({
    user: process.env.USER_MAIL,
    password: process.env.PASS_MAIL,
    host: 'smtp.gmail.com',
    ssl: true,
});
const sendMessage = (destiny) => {
    const message = new emailjs_1.Message({
        text: '!!Registro Exitoso¡¡',
        from: 'ReportesBOG <testingnodejs8@gmail.com>',
        to: destiny,
        subject: 'Has completado tu registro',
        attachment: {
            data: `Te damos la bienvenida a ReportesBOG.
Esperamos que la aplicación sea util.`,
            type: 'text/plain'
        }
    });
    return client.send(message, function (err, messag) {
        console.log(err, messag);
    });
};
exports.sendMessage = sendMessage;

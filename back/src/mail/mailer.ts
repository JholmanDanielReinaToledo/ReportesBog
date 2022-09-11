import { Message, SMTPClient } from 'emailjs'

require('dotenv').config()

const client = new SMTPClient({
  user: process.env.USER_MAIL,
  password: process.env.PASS_MAIL,
  host: 'smtp.gmail.com',
  ssl: true,
})

export const sendMessage = (destiny: string): any => {
  const message = new Message({
    text: '!!Registro Exitoso¡¡',
    from: 'ReportesBOG <testingnodejs8@gmail.com>',
    to: destiny,
    subject: 'Has completado tu registro',
    attachment: {
      data:
`Te damos la bienvenida a ReportesBOG.
Esperamos que la aplicación sea util.`,
      type: 'text/plain'
    }
  })
  return client.send(message, function (err, messag) {
    console.log(err, messag)
  })
}

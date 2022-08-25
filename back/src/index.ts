import express from 'express'
import cors, { CorsOptions } from 'cors'
import { postgraphile } from 'postgraphile'

require('dotenv').config()

const app = express()

console.log(process.env.CONNECTION)

const options: CorsOptions = {
  origin: '*',
};

app.use(cors(options));

app.use(
  '/back',
  postgraphile(
    process.env.CONNECTION,
    [
      'administracion',
      'aplicacion',
      'dominios',
    ],
    {
      watchPg: true,
      graphiql: true,
      enhanceGraphiql: true,
    }
  )
);

app.listen(process.env.PORT, () => {
  console.log('Server running in port ' + process.env.PORT)
});

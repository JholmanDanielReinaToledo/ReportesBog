import express from 'express'
import cors, { CorsOptions } from 'cors'
import { postgraphile } from 'postgraphile'
import router from './routes/security'

require('dotenv').config()

const app = express()

const options: CorsOptions = {
  origin: '*',
};

app.use(cors(options));

app.use(router)

app.use(
  '/back',
  postgraphile(
    process.env.DB_URL,
    [
      'administracion',
      'aplicacion',
      'dominios',
      'public'
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

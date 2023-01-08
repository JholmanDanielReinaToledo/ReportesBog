import express from 'express'
import cors, { CorsOptions } from 'cors'
import { postgraphile } from 'postgraphile'
import postgis from '@graphile/postgis';
import router from './routes/security'
import bodyParser from 'body-parser'
import { Pool } from 'pg';

require('dotenv').config()

const app = express()

const options: CorsOptions = {
  origin: '*',
};

const pool = new Pool({
  host: process.env.HOST_DB,
  user: process.env.USER_DB,
  port: 5432,
  password: process.env.PASS_DB,
  max: 20,
  idleTimeoutMillis: 30000,
  connectionTimeoutMillis: 2000,
});

app.use(bodyParser.json())
app.use(cors(options));

app.use(router)

app.use(
  '/back',
  postgraphile(
    pool,
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
      appendPlugins: [postgis],
    }
  )
);

app.listen(process.env.PORT, () => {
  console.log('Server running in port ' + process.env.PORT)
});

import express from 'express'
import cors from 'cors'
import { postgraphile } from 'postgraphile'

const app = express()
const PORT = 3000;  // URL	jdbc:postgresql://localhost:5434/postgres
const CONNECTION_STRING = 'postgres://postgres:juanesloco123@localhost:5434/postgres'


const options = {
  origin: '*',
};

app.use(cors(options));

app.use(
  '/back',
  postgraphile(
    CONNECTION_STRING,
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

app.listen(PORT, () => {
  console.log('Server running in port ' + PORT)
});

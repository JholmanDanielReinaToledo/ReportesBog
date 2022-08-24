const express = require("express");
const { postgraphile  } = require("postgraphile");
const cors = require('cors');
const postgis = require('@graphile/postgis');
const path = require('path');

const app = express();

const options = {
    origin: '*',
};

const CONNECTION_DB = 'postgres://postgres:juanesloco123@localhost:5432/postgres'

app.use(cors(options));

app.use(express.json({ limit: '100mb' }));

app.use(express.static(path.join(__dirname, '../public')));

app.use('/graph',
  postgraphile(
    CONNECTION_DB,
    "public",
    {
      retryOnInitFail: true,
      watchPg: true,
      graphiql: true,
      enhanceGraphiql: true,
      appendPlugins: [postgis],
    }
  )
);

app.listen(3000);
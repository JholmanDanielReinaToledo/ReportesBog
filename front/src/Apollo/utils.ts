import { ApolloClient, InMemoryCache } from '@apollo/client';
import { ApolloLink, ApolloQueryResult } from '@apollo/client/core';
import { isEmpty } from 'lodash';

const direccion = 'http://3.83.162.59:5433/';

const cache = new InMemoryCache();

const client = new ApolloClient({
  uri: `${direccion}back/graphql`,
  cache,
});

export default client;

import { InMemoryCache } from "apollo-cache-inmemory";
import fetch from 'node-fetch';
import ApolloClient, { ApolloQueryResult, DefaultOptions } from "apollo-client";
import { HttpLink } from "apollo-link-http";
import { isEmpty, isString } from "lodash";
import { Request, Response } from 'express';
import QueryString from 'qs';

export type Req = Request<any, any, any, QueryString.ParsedQs, Record<string, any>>;
export type Res = Response<any, any>;

const direccion = 'http://localhost:5000/';

const link = new HttpLink({
  uri: `${direccion}back/graphql`,
  // @ts-ignore
  fetch,
});

const cache = new InMemoryCache();

const defaultOptions: DefaultOptions = {
  watchQuery: {
    fetchPolicy: 'no-cache',
    errorPolicy: 'ignore',
  },
  query: {
    fetchPolicy: 'no-cache',
    errorPolicy: 'all',
  },
};

const client = new ApolloClient({
  link,
  cache,
  defaultOptions,
});

const genericOperation = <T, >(opType: 'mutate' | 'query') => async (
  gql: any,
  variables?: any,
  res?: Res,
): Promise<ApolloQueryResult<T> | any> => {
  try {
    const operation = await (opType === 'mutate' ? client.mutate<T>({
      mutation: gql,
      variables,
    }) : client.query<T>({
      query: gql,
      variables,
    }));

    if (res) {
      return res.send(operation);
    }
    if (!isEmpty(operation.errors)) {
      console.log(operation);
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      throw Error(operation.errors.map((e) => e.message));
    }
    return operation;
  } catch (error) {
    console.log(error, variables);
    if (res) {
      return res.status(403).send(error);
    }
    if (isString(error)) throw Error(error);
  }
};

export const mutate = genericOperation('mutate');
export const query = genericOperation('query');

export default client;

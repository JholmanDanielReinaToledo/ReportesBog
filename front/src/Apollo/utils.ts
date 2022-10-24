import { InMemoryCache } from "apollo-cache-inmemory";
import ApolloClient, { ApolloQueryResult, DefaultOptions } from "apollo-client";
import { HttpLink } from "apollo-link-http";
import { isEmpty, isString } from "lodash";
import QueryString from 'qs';

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
): Promise<ApolloQueryResult<T> | any> => {
  try {
    const operation = await (opType === 'mutate' ? client.mutate<T>({
      mutation: gql,
      variables,
    }) : client.query<T>({
      query: gql,
      variables,
    }));

    if (!isEmpty(operation.errors)) {
      console.log(operation);
      // @ts-ignore
      throw Error(operation.errors.map((e) => e.message));
    }
    return operation;
  } catch (error) {
    console.log(error, variables);
    if (isString(error)) throw Error(error);
  }
};

export const mutate = genericOperation('mutate');
export const query = genericOperation('query');

export default client;

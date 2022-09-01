"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.query = exports.mutate = void 0;
const apollo_cache_inmemory_1 = require("apollo-cache-inmemory");
const node_fetch_1 = __importDefault(require("node-fetch"));
const apollo_client_1 = __importDefault(require("apollo-client"));
const apollo_link_http_1 = require("apollo-link-http");
const lodash_1 = require("lodash");
const direccion = 'http://localhost:5000/';
const link = new apollo_link_http_1.HttpLink({
    uri: `${direccion}back/graphql`,
    // @ts-ignore
    fetch: node_fetch_1.default,
});
const cache = new apollo_cache_inmemory_1.InMemoryCache();
const defaultOptions = {
    watchQuery: {
        fetchPolicy: 'no-cache',
        errorPolicy: 'ignore',
    },
    query: {
        fetchPolicy: 'no-cache',
        errorPolicy: 'all',
    },
};
const client = new apollo_client_1.default({
    link,
    cache,
    defaultOptions,
});
const genericOperation = (opType) => (gql, variables, res) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        const operation = yield (opType === 'mutate' ? client.mutate({
            mutation: gql,
            variables,
        }) : client.query({
            query: gql,
            variables,
        }));
        if (res) {
            return res.send(operation);
        }
        if (!(0, lodash_1.isEmpty)(operation.errors)) {
            console.log(operation);
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-ignore
            throw Error(operation.errors.map((e) => e.message));
        }
        return operation;
    }
    catch (error) {
        console.log(error, variables);
        if (res) {
            return res.status(403).send(error);
        }
        if ((0, lodash_1.isString)(error))
            throw Error(error);
    }
});
exports.mutate = genericOperation('mutate');
exports.query = genericOperation('query');
exports.default = client;

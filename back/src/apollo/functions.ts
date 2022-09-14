import { User } from "../types";
import { GET_USER_BY_IDENTIFICACION, INSERT_NEW_USER } from "./gql";
import { mutate, query, Res } from "./utils";

export const insertNewUser = (user: User, res: Res) => mutate(
  INSERT_NEW_USER,
  { data: {
    usuario: user
  } },
  res,
);

export const getUserByIdentificacion = (identificacion: string) => query(
  GET_USER_BY_IDENTIFICACION,
  {
    identificacion,
  }
)


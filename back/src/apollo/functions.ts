import { User } from "../types";
import { INSERT_NEW_USER } from "./gql";
import { mutate, Res } from "./utils";

export const insertNewUser = (user: User, res: Res) => mutate(
  INSERT_NEW_USER,
  { data: {
    usuario: user
  } },
  res,
);

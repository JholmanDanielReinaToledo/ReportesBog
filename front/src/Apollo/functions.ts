import { GET_ALL_INCONVENIENTES } from "../graphql/querys";
import { query } from "./utils";

export const getAllInconvenientes = () => query(
  GET_ALL_INCONVENIENTES,
)
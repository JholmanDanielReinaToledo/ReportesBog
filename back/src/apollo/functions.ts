import { string } from "joi";
import { Report, User } from "../types";
import { CREATE_NEW_REPORT, GET_BARRIOS_BY_ID_LOCALIDAD, GET_LOCALIDADES, GET_USER_BY_IDENTIFICACION, INSERT_NEW_USER } from "./gql";
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

export const createNewReport = (report: Report , res:Res) => mutate(
  CREATE_NEW_REPORT,
    {
      data:{
        inconveniente: report
      }
    },
  res,
)


export const getLocalidades = () => query(
  GET_LOCALIDADES
)
  
  export const getBarrios = (idLocalidad:number) => query(
  GET_BARRIOS_BY_ID_LOCALIDAD,
  {
    idLocalidad,
  }
)
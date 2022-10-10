import { message } from "antd";
import axios from "axios";
import { UsuarioLogin } from "../common/types";

const BASE_URL = 'http://localhost:5000/';

export const login = async (usuarioLogin: UsuarioLogin) => {
  if (usuarioLogin.usuario && usuarioLogin.password) {
    const { data } = await axios.post(BASE_URL + 'login', 
    {
      password: usuarioLogin.password,
      identificacion: usuarioLogin.usuario,
    }
    );
    return data;
  }
  return false;
};

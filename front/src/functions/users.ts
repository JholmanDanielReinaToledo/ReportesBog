import { message } from "antd";
import axios from "axios";
import { UsuarioLogin } from "../common/types";

const BASE_URL = 'http://3.83.162.59:5433/';

export const login = async (usuarioLogin: UsuarioLogin) => {
  if (usuarioLogin.usuario && usuarioLogin.password) {
    return axios.post(BASE_URL + 'login', 
      {
        password: usuarioLogin.password,
        identificacion: usuarioLogin.usuario,
      }
    )
      .then(({data}) => data)
      .catch(() => message.info('Usuario o contraseña incorrectos'));
  }
  return false;
};

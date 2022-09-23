import { UsuarioLogin } from "../common/types";

const BASE_URL = 'http://localhost:5000/'
export const login = async (usuarioLogin: UsuarioLogin) => {
  if (usuarioLogin.usuario && usuarioLogin.password) {
    fetch(
      BASE_URL + 'login',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          password: usuarioLogin.password,
          identificacion: usuarioLogin.usuario,
        }),
      }
    )
      .then((res) => console.log(res))
      .catch((err) => console.log(err))
  }
}
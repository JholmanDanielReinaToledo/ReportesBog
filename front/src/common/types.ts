export type UsuarioLogin = {
  id: number;
  nombre: string;
  apellido: string;
  idTipoDocumento: number;
  identificacion: string;
  correoElectronico: string;
  usuario: string;
  password: string;
  activo: boolean;
  idGrupo: number;
  grupoByIdGrupo: {
    nombre: string;
  }
};

export type UsuarioLoginToken = {
  id: string;
  identificacion: string;
  correoElectronico: string;
  password: string;
  activo: string;
  __typename: string;
  token: string;
}

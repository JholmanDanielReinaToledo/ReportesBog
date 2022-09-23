export type UsuarioLogin = {
  id: number;
  nombre: string;
  apellido: string;
  idTipoDocumento: number;
  identificacion: string;
  correoElectronico: string;
  usuario: string;
  password: string;
  esActivo: boolean;
  idGrupo: number;
};
export type User = {
  id: number;
  nombre: string;
  apellido: string;
  id_tipo_documento: number;
  identificacion: string;
  correo_electronico: string;
  password: string;
  activo: string;
  id_grupo: number;
}

export type Report = {
  id: number;
  idUsuario: number;
  idEstado: number;
  descripcion: string;
}

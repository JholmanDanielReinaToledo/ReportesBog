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

// nombre varchar not null,
// apellido varchar not null,
// id_tipo_documento bigint not null references dominios.tipo_documento(id),
// identificacion varchar not null unique,
// correo_electronico varchar,
// password varchar,
// activo boolean default false,
// id_grupo bigint references administracion.grupo(id)
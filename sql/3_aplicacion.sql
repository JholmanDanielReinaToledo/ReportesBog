create table aplicacion.direccion(
  id bigserial primary key,
  cruce_desde bigint not null references dominios.nomenclatura_vial(id),
  numero_desde int not null,
  letra_desde varchar,
  orientacion_desde bigint references dominios.orientacion(id),
  cruce_hasta bigint not null references dominios.nomenclatura_vial(id),
  numero_hasta int not null,
  letra_hasta varchar,
  orientacion_hasta bigint references dominios.orientacion(id),
  numero int not null,
  complemento varchar,
  id_barrio bigint references dominios.barrio(id),
  id_localidad bigint references dominios.localidad(id),
  localizacion geometry(pointz)
);

create index on aplicacion.direccion(id_barrio);
create index on aplicacion.direccion(id_localidad);

create table aplicacion.inconveniente (
  id bigserial primary key,
  descripcion text,
  id_estado bigint not null references dominios.estado_reporte(id),
  fecha_creacion timestamp not null default current_timestamp,
  id_direccion bigint unique references aplicacion.direccion(id),
  id_usuario bigint not null references administracion.usuario(id)
);

create index on aplicacion.inconveniente(id_direccion);
create index on aplicacion.inconveniente(id_usuario);
create index on aplicacion.inconveniente(id_estado);

create table aplicacion.foto(
  id bigserial primary key,
  id_inconveniente bigint not null references aplicacion.inconveniente(id),
  enlace text not null
);

create index on aplicacion.foto(id_inconveniente);

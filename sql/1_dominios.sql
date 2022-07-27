create table dominios.tipo_documento (
  id bigserial primary key,
  descripcion varchar
);

create table dominios.orientacion (
  id bigserial primary key,
  descripcion varchar
);

create table dominios.nomenclatura_vial (
  id bigserial primary key,
  descripcion varchar
);

create table dominios.estado_reporte (
  id bigserial primary key,
  descripcion varchar
);

create table dominios.barrio (
  id bigserial primary key,
  descripcion varchar
);

create table dominios.localidad (
  id bigserial primary key,
  descripcion varchar
);

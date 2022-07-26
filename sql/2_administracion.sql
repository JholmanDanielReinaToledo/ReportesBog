create table administracion.modulo_sistema(
    id bigserial primary key,
    nombre varchar not null,
    descripcion varchar
);

create table administracion.grupo(
	id bigserial primary key,
	nombre varchar not null,
	descripcion varchar not null
);

create table administracion.usuario(
	id bigserial primary key,
	nombre varchar not null,
	apellido varchar not null,
	id_tipo_documento bigint not null references dominios.tipo_documento(id),
	identificacion varchar not null unique,
	correo_electronico varchar,
	password varchar,
	activo boolean default false,
  id_grupo bigint references administracion.grupo(id)
);

create index on administracion.usuario(id_grupo);

create table administracion.permisos_x_modulo_x_grupo(
	id bigserial primary key,
	leer boolean not null default false,
	escribir boolean not null default false,
	borrar boolean not null default false,
	id_modulo_sistema bigint not null references administracion.modulo_sistema(id),
	id_grupo bigint not null references administracion.grupo(id) on delete cascade,
  unique(id_modulo_sistema, id_grupo)
);

create index on administracion.permisos_x_modulo_x_grupo(id_modulo_sistema);
create index on administracion.permisos_x_modulo_x_grupo(id_grupo);

create  or replace function tgg_prohibido_eliminar_grupo_admin() returns trigger as $$
	declare
	begin
		if Old.id in (0, 1, 2) then
		raise exception 'No se pueden eliminar los grupos de Administrador, Asociación y Productor';
		end if;
	return old;
    end;
$$ language plpgsql strict security definer;

create  trigger tgg_prohibido_eliminar_grupo_admin before delete
	on administracion.grupo For EACH row
	execute procedure tgg_prohibido_eliminar_grupo_admin();

create  or replace function tgg_prohibido_eliminar_usuario_admin() returns trigger as $$
	declare
	begin
		if Old.id = 0 then
		raise exception 'No se puede eliminar el usuario Administrador';
		end if;
	return old;
    end;
$$ language plpgsql strict security definer;

create  trigger tgg_prohibido_eliminar_usuario_admin before delete
	on administracion.usuario For EACH row
	execute procedure tgg_prohibido_eliminar_usuario_admin();

/* tipo documento */
INSERT INTO dominios.tipo_documento (descripcion) VALUES
	 ('Cédula de ciudadanía'),
	 ('Cédula de extranjeria'),
	 ('Pasaporte'),
	 ('Tarjeta de identidad'),
	 ('Registro civil'),
	 ('NUIP'),
	 ('NIT'),
	 ('NIP');

insert into dominios.estado_reporte (descripcion) values 
  ('Pendiente'),
  ('En proceso'),
  ('Revisado'),
  ('Rechazado'),
  ('Sin estado');

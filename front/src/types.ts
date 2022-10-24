export type Cruce = {
  descripcion: string;
}

export type Orientacion = {
  descripcion: string;
}

export type EstadoReporte = {
  descripcion: string;
}

export type Barrio = {
  descripcion: string,
}

export type Localidad = {
  descripcion: string,
}


export type Direccion = {
  id: string,
  cruceDesde: Cruce;
  numeroDesde: string,
  letraDesde: string,
  orientacionDesde: Orientacion,
  cruceHasta: Cruce,
  numeroHasta: string,
  letraHasta: string,
  orientacionHasta: Orientacion,
  numero: string,
  complemento: string,
  localizacion: string,
  barrio: Barrio,
  localidad: Localidad,
}

export type Inconveniente = {
  id: string,
  descripcion: string,
  fechaCreacion: Date,
  estadoReporteByIdEstado: EstadoReporte,
  direccionByIdDireccion: Direccion,
};
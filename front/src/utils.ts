import { Direccion } from "./types";

export const getStringDireccion = (direccion: Direccion) => `${direccion.cruceDesde.descripcion} ${direccion.numeroDesde} ${direccion.letraDesde ?? ''} # ${direccion.numeroHasta} ${direccion.letraHasta} ${direccion.numero}`;
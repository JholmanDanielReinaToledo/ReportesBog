import gql from 'graphql-tag';

export const GET_ALL_INCONVENIENTES = gql`
query getAllInconvenientes {
  allInconvenientes {
    nodes {
      id
      descripcion
      fechaCreacion
      estadoReporteByIdEstado {
        descripcion
      }
      direccionByIdDireccion {
        id
        cruceDesde: nomenclaturaVialByCruceDesde {
          descripcion
        }
        numeroDesde
        letraDesde
        orientacionDesde: orientacionByOrientacionDesde {
          descripcion
        }
        cruceHasta: nomenclaturaVialByCruceHasta {
          descripcion
        }
        numeroHasta
        letraHasta
        orientacionHasta: orientacionByOrientacionHasta {
          descripcion
        }
        numero
        complemento
        localizacion
        barrio: barrioByIdBarrio {
          descripcion
        } 
        localidad: localidadByIdLocalidad {
          descripcion
        }
      }
    }
  }
}`;

export const GET_INCONVENIENTE_BY_ID = gql`
query getInconvenienteById($id: BigInt!) {
  inconvenienteById(id: $id) {
      id
      descripcion
      fechaCreacion
      estadoReporteByIdEstado {
        descripcion
      }
      direccionByIdDireccion {
        id
        cruceDesde: nomenclaturaVialByCruceDesde {
          descripcion
        }
        numeroDesde
        letraDesde
        orientacionDesde: orientacionByOrientacionDesde {
          descripcion
        }
        cruceHasta: nomenclaturaVialByCruceHasta {
          descripcion
        }
        numeroHasta
        letraHasta
        orientacionHasta: orientacionByOrientacionHasta {
          descripcion
        }
        numero
        complemento
        localizacion
        barrio: barrioByIdBarrio {
          descripcion
        } 
        localidad: localidadByIdLocalidad {
          descripcion
        }
      }
    }
  }
`

export const GET_ALL_USUARIOS = gql`
query getAllUsuarios {
  allUsuarios{
    nodes {
      id
      nombre
      apellido
      idTipoDocumento
      identificacion
      correoElectronico
      activo
      grupoByIdGrupo {
        nombre
      }
    }
  }
}

`;
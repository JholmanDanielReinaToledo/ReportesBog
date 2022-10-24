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

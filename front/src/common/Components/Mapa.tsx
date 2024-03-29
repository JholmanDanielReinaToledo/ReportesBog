import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet';
import IconLocation from './IconLocation';
import { FireOutlined } from '@ant-design/icons';
import { divIcon } from 'leaflet';
import { map } from 'lodash';

const icon = divIcon({ className: 'leaflet-point-icon' });

const Mapa = ({
  puntos,
}: {
  puntos?: any[],
}) => {
  // 4.664784406288559, -74.09862812026168
  // <iframe width="425" height="350" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src="https://www.openstreetmap.org/export/embed.html?bbox=-74.45400238037111%2C4.508318306218056%2C-73.88065338134767%2C4.821075515150199&amp;layer=transportmap" style="border: 1px solid black"></iframe><br/><small><a href="https://www.openstreetmap.org/#map=12/4.6647/-74.1673&amp;layers=T">Ver mapa más grande</a></small>
  return ( // https://www.openstreetmap.org/#map=12/4.6647/-74.1673&layers=T
    <>
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.0.1/dist/leaflet.css" />
      <div id="map">
        <MapContainer center={[4.66478, -74.098628]} zoom={12} scrollWheelZoom={true} style={{height: '82vh'}}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {
            map(puntos, (punto) => {
              return (<Marker position={[punto.x, punto.y]} />)
            })
          }
          
        </MapContainer>
      </div>
    </>
  )
}

export default Mapa;

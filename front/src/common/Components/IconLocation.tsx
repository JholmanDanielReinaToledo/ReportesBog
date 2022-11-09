import L from 'leaflet';

const IconLocation = L.icon({
  iconUrl: require('../../../public/location.svg'),
  iconRetinaUrl: require('../../../public/location.svg'),
  iconAnchor: undefined,
  shadowUrl: undefined,
  shadowSize: undefined,
  shadowAnchor: undefined,
  iconSize: [35, 35],
  className: "leaflet-venue-icon",
});

export default IconLocation;

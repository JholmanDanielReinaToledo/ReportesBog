## Primero se crea la red

podman network create --driver=bridge --subnet=10.1.1.0/16 sisdep-network

## Segundo se crea el volumen para almacenamiento de archivos

podman volume create archivos --driver=local --opt device=/archivos --opt type=none --opt o=bind

## Tercero se crean los contenedores del sistema

podman run -d --network=sisdep-network --ip=10.1.100.1 --name=sisdep-front --network-alias=front --restart=unless-stopped ID_IMAGEN_FRONTEND
podman run -d --network=sisdep-network --ip=10.1.100.2 --name=sisdep-back --network-alias=back --restart=unless-stopped --mount=type=volume,source=archivos,destination=/archivos,rw=true ID_IMAGEN_BACKEND

## Cuarto se crea el contenedor NGINX

podman run -d --network=sisdep-network -p 80:80 --name=sisdep-webserver --network-alias=webserver --restart=unless-stopped ID_IMAGEN_WEBSERVER
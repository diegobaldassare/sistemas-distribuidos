# Sistemas Distribuidos 2021
### Requerimientos
* Docker

### Correr
#####Si es la primera vez:
* `docker network create custom_network` => Crea una red para que se comuniquen los servicios

#####Background:
* `docker-compose up -d` => levanta por detras todos los servicios
* `docker-compose up -d auth` => levanta por detras solo el servicio de authenticación
* `docker-compose up -d geo` => levanta por detras solo el servicio de geolocalización

#####Normal (para cortar el proceso `CTRL + C`):
* `docker-compose up` => levanta todos los servicios
* `docker-compose up auth` => levanta solo el servicio de authenticación
* `docker-compose up geo` => levanta solo el servicio de geolocalización

#####Ver logs (los procesos corren por detras):
######Servicio de autenticación:
* `docker logs auth_server` => Muestra todos los logs del servicio
* `docker logs -f auth_server` => Se conecta a la consola del servicio y muestra todos los logs
* `docker logs --tail 10 auth_server` => Muestra los últimos 10 logs del servicio
* `docker logs -f --tail 10 auth_server` => Se conecta a la consola del servicio a partir de los últimos 10 logs

######Servicio de geolocalización:
* `docker logs geo_server` => Muestra todos los logs del servicio
* `docker logs -f geo_server` => Se conecta a la consola del servicio y muestra todos los logs
* `docker logs --tail 10 geo_server` => Muestra los últimos 10 logs del servicio
* `docker logs -f --tail 10 geo_server` => Se conecta a la consola del servicio a partir de los últimos 10 logs

Para cortar el proceso: `CTRL + C`

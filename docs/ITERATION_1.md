# Sistemas Distribuidos 2021
- - -
### Cuestionario

1) El servicio hoy en día no es escalable ya que no se puede modificar la cantidad de "stubs" en runtime. 
   O bien, es posible hacerlo, pero el **client** necesita tener una referencia para poder comunicarse 
   con los servidores.
   
2) ...

3) No porque no hay acceso a datos o movimiento de datos del cliente dentro del sistema en esta primera 
   iteración.

4) Nuestro primer intento de un método de fail-over es que el cliente tome un nuevo stub de forma aleatoria.
   De esta manera, si hay un problema en la comunicación entre el cliente y el stub, el cliente no se percata
   de esta falla y que la comunicación ahora pasa a otro stub aleatorio.

5) El contrato del servicio está definido en los archivos **.proto** de protocol buffer. Allí se define
   la estructura de los mensajes y las interfaces de los servicios. En lo que respecta al encapsulamiento,
   esta forma de definir servicios permitió que cada uno quede definido en su propio paquete sin 
   dependencias externas.
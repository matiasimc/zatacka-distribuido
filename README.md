# zatacka-distribuido

## Instrucciones de ejecución

./runserver.sh IP -n esperarJugadores: Ejecuta el servidor con dirección IP, opcionalmente se puede indicar la cantidad de jugadores minimos requeridos para iniciar la partida, con la opcion -n.

./closeserver.sh: Cierra el puerto del ultimo servidor abierto, necesario para ejecutar nuevamente el servidor

./runclient.sh IP: Inicia un cliente que se conectará a la partida del servidor de dirección IP.

## Características

- Soporta un máximo de 5 jugadores, los cuales se pueden unir a una partida en cualquier momento.
- Permite indicar la cantidad mínima de jugadores para iniciar una partida al momento de lanzar el servidor.
- Muestra una listado con los puntajes de cada jugador al terminar la partida.

## Cómo jugar

El movimiento del jugador se controla con las teclas UP y DOWN.

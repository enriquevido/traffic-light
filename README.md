# Simulador de semaforos con hilos

Este proyecto es una aplicacion de escritorio que simula dos semaforos funcionando al mismo tiempo. Cada uno cambia de color de forma independiente en diferentes intervalos.

La logica del programa se apoya en hilos para que ambos semaforos trabajen de manera concurrente, respetando sus propios intervalos de tiempo. La UI se hizo con Java Swing.

## Ejecucion

```bash
javac SemaforosApp.java
java SemaforosApp
```
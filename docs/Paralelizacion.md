# Informe de Paralelización

**Integrantes:** 

| Nombre completo | Código | Correo institucional |
|-----------------|--------|----------------------|
| Juan Sebastian Navarrete Rada | 202459562 | juan.sebastian.navarrete@correounivalle.edu.co |
| María Fernanda González Ramírez  | 202477325 | maria.gonzalez.r@correounivalle.edu.co |
| Samuel Garcia Parra  | 202459476 | samuel.parra@correounivalle.edu.co |


---

# Estrategia de paralelización

El objetivo de la paralelización fue reducir el tiempo de ejecución de la búsqueda de la asignación óptima de aulas utilizando las funciones `parallel` y `task` suministradas por el paquete `common`.

La estrategia general consistió en dividir los problemas en subproblemas independientes que pudieran ejecutarse simultáneamente sobre distintos núcleos del procesador, para posteriormente combinar los resultados parciales.

## Paralelización de `choquesPar`

La función `choquesPar` divide el conjunto de cursos en dos mitades.

Cada mitad calcula en paralelo los choques producidos entre los cursos pertenecientes a su rango correspondiente. Después se calculan los choques que ocurren entre elementos de diferentes mitades (frontera) y finalmente se suman todos los resultados.

Esta estrategia sigue el enfoque de **divide y vencerás**, aprovechando que el conteo de choques puede realizarse de forma independiente sobre subconjuntos de cursos.

## Paralelización de `desperdicioPar`

La función `desperdicioPar` divide el vector de cursos en dos rangos.

Cada rango calcula de manera independiente el desperdicio de capacidad producido por las asignaciones correspondientes a sus cursos. Como cada curso puede evaluarse sin depender de los demás, se trata de un problema con paralelismo natural de datos.

Finalmente se suman los resultados parciales para obtener el desperdicio total.

## Paralelización de `movilidadPar`

La función `movilidadPar` utiliza una estrategia recursiva.

Después de ordenar los cursos por hora de inicio, la secuencia resultante se divide recursivamente en dos partes. Cada mitad calcula su costo de movilidad en paralelo y posteriormente se añade el costo correspondiente a la frontera entre ambas particiones.

Esta estrategia permite distribuir el cálculo de las distancias entre varios procesadores.

## Paralelización de `generarAsignacionesPar`

La generación de asignaciones es una de las operaciones más costosas debido a que explora un espacio de búsqueda de tamaño:

$$
m^n
$$

donde:

* $n$ es el número de cursos.
* $m$ es el número de aulas.

La paralelización se realizó dividiendo los posibles valores del primer curso en dos grupos.

Cada grupo genera en paralelo todas las asignaciones derivadas de sus valores iniciales y posteriormente ambos conjuntos son concatenados para formar el resultado completo.

## Paralelización de `asignacionOptimaPar`

La función `asignacionOptimaPar` constituye la principal optimización del proyecto.

Después de generar todas las asignaciones posibles, el conjunto de candidatos se divide en dos mitades.

Cada mitad busca en paralelo la asignación con menor costo dentro de su rango utilizando una búsqueda secuencial local. Una vez obtenidos ambos mínimos locales, se comparan para seleccionar la mejor solución global.

Esta estrategia permite reducir significativamente el tiempo invertido en la exploración exhaustiva del espacio de búsqueda.

---

# Resultados experimentales

Las mediciones fueron realizadas utilizando la biblioteca `org.scalameter`.

| Cursos (n) | Aulas (m) | Secuencial (ms) | Paralela (ms) | Aceleración (%) |
| ---------: | --------: | --------------: | ------------: | --------------: |
|          2 |         2 |           0.517 |         0.882 |          -70.39 |
|          3 |         2 |           0.535 |         1.038 |          -93.96 |
|          3 |         3 |           2.291 |         1.358 |           40.73 |
|          4 |         3 |           1.705 |         1.698 |            0.40 |
|          5 |         3 |           2.922 |         2.758 |            5.61 |
|          6 |         3 |           7.462 |         5.403 |           27.59 |
|          6 |         4 |          40.178 |        30.154 |           24.95 |
|          7 |         4 |          40.062 |        19.460 |           51.43 |

La aceleración fue calculada mediante la expresión:

$$
Aceleración = \frac{T_{sec} - T_{par}}{T_{sec}} \times 100
$$

donde:

* $T_{sec}$ corresponde al tiempo de ejecución secuencial.
* $T_{par}$ corresponde al tiempo de ejecución paralelo.

---

# Análisis con la ley de Amdahl

La Ley de Amdahl establece que la aceleración máxima alcanzable mediante paralelización está dada por:

$$
S(p)=\frac{1}{(1-\alpha)+\frac{\alpha}{p}}
$$

donde:

* $S(p)$ representa el speedup.
* $p$ es el número de procesadores.
* $\alpha$ es la fracción paralelizable del programa.

Las etapas más costosas de nuestra solución son:

1. Generación de asignaciones.
2. Evaluación de costos.
3. Búsqueda de la asignación óptima.

Estas fueron precisamente las regiones paralelizadas, por lo que una gran parte del tiempo total de ejecución pertenece a la fracción paralelizable del algoritmo.

Sin embargo, existen componentes secuenciales que no pueden eliminarse completamente:

* Creación de estructuras de datos.
* Combinación de resultados.
* Cálculo de fronteras.
* Sincronización entre tareas paralelas.

Estas partes limitan la aceleración máxima alcanzable.

## Estimación de la fracción paralelizable

Tomando el mejor resultado obtenido:

| Cursos | Aulas | Tiempo secuencial | Tiempo paralelo |
| ------ | ----- | ----------------- | --------------- |
| 7      | 4     | 40.062 ms         | 19.460 ms       |

El speedup correspondiente es:

$$
S=\frac{T_{sec}}{T_{par}}
$$

$$
S=\frac{40.062}{19.460}
$$

$$
S \approx 2.06
$$

Suponiendo dos tareas paralelas principales ($p=2$), la Ley de Amdahl queda:

$$
2.06=\frac{1}{(1-\alpha)+\frac{\alpha}{2}}
$$

Despejando:

$$
\frac{1}{2.06}=1-\frac{\alpha}{2}
$$

$$
0.4854=1-\frac{\alpha}{2}
$$

$$
\frac{\alpha}{2}=0.5146
$$

$$
\alpha \approx 1.03
$$

El valor obtenido es ligeramente superior a 1 debido a variaciones experimentales y errores de medición. En la práctica esto indica que la fracción paralelizable del programa es extremadamente alta y se encuentra muy cercana al 100%.

Este resultado es coherente con la implementación desarrollada, ya que las operaciones más costosas fueron precisamente las que se paralelizaron.

## Casos pequeños

Las instancias:

* (2,2)
* (3,2)

presentan aceleraciones negativas.

En estos casos el trabajo realizado es demasiado pequeño para compensar el costo de creación, sincronización y coordinación de tareas paralelas.

La sobrecarga introducida por el paralelismo supera la ganancia obtenida.

## Casos intermedios

Las instancias:

* (3,3)
* (4,3)
* (5,3)
* (6,3)

comienzan a mostrar beneficios.

A medida que aumenta el número de asignaciones posibles, el costo del problema crece y el trabajo paralelo empieza a compensar la sobrecarga inicial.

## Casos grandes

Las mayores ganancias se obtuvieron en:

* (6,4)
* (7,4)

donde las aceleraciones alcanzaron aproximadamente el 25% y el 51%, respectivamente.

En estas configuraciones el espacio de búsqueda es considerablemente más grande y la distribución del trabajo entre varios núcleos resulta mucho más efectiva.

El caso (7,4) produjo el mejor resultado observado, reduciendo aproximadamente a la mitad el tiempo de ejecución respecto a la versión secuencial.

---

# Conclusiones de paralelización

La paralelización implementada permitió reducir significativamente los tiempos de ejecución para instancias medianas y grandes del problema.

Los experimentos muestran que en problemas pequeños la versión secuencial continúa siendo más eficiente debido a la sobrecarga asociada a la creación y sincronización de tareas concurrentes.

A medida que aumenta el tamaño del espacio de búsqueda, la versión paralela aprovecha mejor los recursos disponibles del procesador y comienza a generar aceleraciones importantes.

Los resultados obtenidos confirman lo esperado por la Ley de Amdahl: aunque siempre existe una parte secuencial que limita la aceleración máxima posible, la paralelización de las regiones más costosas del programa permite obtener mejoras significativas en el rendimiento.

Finalmente, se concluye que la versión paralela es la alternativa más adecuada para instancias de tamaño medio y grande, mientras que para entradas pequeñas resulta preferible utilizar la versión secuencial debido a su menor costo de ejecución.

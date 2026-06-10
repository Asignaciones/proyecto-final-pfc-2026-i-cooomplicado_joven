# Conclusiones

**Integrantes:**

| Nombre completo | Código | Correo institucional |
|-----------------|--------|----------------------|
| Juan Sebastian Navarrete Rada | 202459562 | juan.sebastian.navarrete@correounivalle.edu.co |
| María Fernanda González Ramírez  | 202477325 | maria.gonzalez.r@correounivalle.edu.co |
| Samuel Garcia Parra  | 202459476 | samuel.parra@correounivalle.edu.co |

---

# Conclusiones del proyecto

## Programación funcional

La implementación de la solución utilizando programación funcional permitió desarrollar un código más declarativo y cercano a la definición matemática del problema. El uso de recursión, funciones de orden superior como `map`, `flatMap`, `foldLeft`, `filter` y colecciones inmutables facilitó la construcción de soluciones sin depender de variables mutables o ciclos iterativos.

Entre las principales ventajas encontradas se destaca la facilidad para razonar sobre la corrección de las funciones, ya que cada una se construye a partir de transformaciones bien definidas sobre estructuras inmutables. Además, el uso de funciones puras favoreció la modularidad y la reutilización del código.

La principal dificultad fue diseñar algunas funciones recursivas, especialmente aquellas relacionadas con la generación de asignaciones y el cálculo de movilidad, ya que fue necesario pensar en términos de descomposición del problema y casos base en lugar de utilizar estructuras iterativas tradicionales. También fue necesario acostumbrarse al uso de funciones de alto orden para reemplazar ciclos y acumuladores mutables.

---

## Corrección

La corrección de las implementaciones se argumentó a partir de las definiciones formales suministradas en el enunciado y mediante el análisis de cada función sobre sus casos base y casos recursivos.

Para las funciones recursivas, como `generarAsignaciones`, se utilizó razonamiento inductivo. Primero se verificó que el caso base produjera el resultado esperado y posteriormente se demostró que, si la función era correcta para un problema de tamaño menor, también producía resultados correctos para el tamaño actual.

En las funciones construidas mediante operaciones sobre colecciones, la corrección se sustentó verificando que cada transformación preservara las propiedades definidas en la especificación. Adicionalmente, se diseñaron múltiples casos de prueba para validar el comportamiento esperado en situaciones normales, casos límite y escenarios especiales.

La combinación de argumentación formal y pruebas permitió obtener un alto grado de confianza en la corrección de la solución implementada.

---

## Paralelismo

Los experimentos realizados muestran que el paralelismo resulta beneficioso cuando el tamaño del problema es suficientemente grande.

Para instancias pequeñas, como las configuraciones con pocos cursos y pocas aulas, la versión paralela presentó tiempos de ejecución mayores que la versión secuencial. Esto se debe a la sobrecarga asociada a la creación, coordinación y sincronización de tareas concurrentes.

Sin embargo, a medida que aumentó el número de cursos y aulas, el espacio de búsqueda creció exponencialmente y el trabajo útil comenzó a superar ampliamente el costo de administración de las tareas paralelas. En estas condiciones, las versiones paralelas lograron reducciones significativas en el tiempo de ejecución.

Los resultados obtenidos confirman que el paralelismo es especialmente útil en problemas con alta carga computacional y una gran cantidad de trabajo independiente que puede ejecutarse simultáneamente.

---

## Aprendizajes

Uno de los principales aprendizajes obtenidos durante el desarrollo del proyecto fue comprender cómo la programación funcional permite modelar problemas complejos mediante funciones puras, recursión y estructuras de datos inmutables.

También se fortaleció el entendimiento sobre el análisis de complejidad algorítmica. El crecimiento exponencial del espacio de búsqueda permitió observar claramente cómo aumenta el costo computacional al incrementar el tamaño de la entrada y por qué resulta necesario aplicar técnicas de optimización como la paralelización.

Otro aspecto importante fue la comprensión práctica de conceptos de concurrencia y paralelismo. Más allá de implementar funciones paralelas, el proyecto permitió analizar cuándo el paralelismo realmente mejora el rendimiento y cuándo la sobrecarga puede convertirlo en una alternativa menos eficiente.

Si se desarrollara nuevamente el proyecto desde cero, sería interesante explorar estrategias más avanzadas para reducir el espacio de búsqueda, como técnicas de poda, heurísticas o algoritmos de optimización que eviten evaluar todas las asignaciones posibles. Esto permitiría resolver instancias de mayor tamaño manteniendo tiempos de ejecución razonables.

---

## Reflexión final

Este proyecto permitió integrar de manera práctica los principales conceptos estudiados durante el curso: programación funcional, recursión, funciones de alto orden, estructuras inmutables, argumentación formal de corrección y paralelización de algoritmos.

La experiencia demostró que la programación funcional no solo proporciona herramientas para construir soluciones correctas y mantenibles, sino que además facilita la incorporación de paralelismo debido a la ausencia de efectos secundarios y al uso de datos inmutables.

Finalmente, el proyecto permitió evidenciar que la elección de una estrategia de diseño adecuada puede tener un impacto significativo tanto en la claridad del código como en el desempeño de la solución.

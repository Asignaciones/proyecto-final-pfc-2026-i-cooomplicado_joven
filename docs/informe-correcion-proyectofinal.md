# Informe de Corrección – Proyecto Final

Informe de corrección – asignacionAulas.scala.
Universidad del Valle – Fundamentos de programación funcional y concurrente.

---

## 1. Función `solapan`

### Implementación

```scala
def solapan(c1: Curso, c2: Curso): Boolean =
  iniCurso(c1) < finCurso(c2) && iniCurso(c2) < finCurso(c1)
```

### Especificación

Dados dos cursos:

$$  
c_1=(id_1,ini_1,fin_1,est_1)  
$$

$$  
c_2=(id_2,ini_2,fin_2,est_2)  
$$

la función debe retornar `true` si y solo si los intervalos

$$  
[ini_1,fin_1)  
$$

y

$$  
[ini_2,fin_2)  
$$

poseen al menos un instante en común.

### Argumento de corrección

Dos intervalos no se traslapan únicamente cuando uno termina antes de que el otro inicie:

$$  
fin_1 \le ini_2  
$$

o

$$  
fin_2 \le ini_1  
$$

Negando dicha condición obtenemos:

$$  
ini_1 < fin_2  
$$

y

$$  
ini_2 < fin_1  
$$

La implementación verifica exactamente estas dos condiciones simultáneamente. Por lo tanto, la función retorna `true` si y solo si existe traslape entre ambos cursos.

---

## 2. Función `choques`

### Implementación

```scala
def choques(cursos: Cursos, a: Asignacion): Int =
  cursos.indices.flatMap { i =>
    (i + 1 until cursos.length).map { j => (i, j) }
  }.count { case (i, j) =>
    a(i) >= 0 && a(i) == a(j) && solapan(cursos(i), cursos(j))
  }
```

### Especificación

Debe calcular el número de pares de cursos que:

1. Están asignados.
2. Comparten la misma aula.
3. Se traslapan en horario.

Formalmente:

$$
CH = \bigl|\{(i,j) \mid i < j,\; a(i) = a(j) \ge 0,\; \text{solapan}(c_i, c_j)\}\bigr|
$$

### Argumento de corrección

La expresión:

```scala
(i + 1 until cursos.length)
```

garantiza que siempre se cumple:

$$  
i < j  
$$

Por tanto, cada par de cursos es generado exactamente una vez.

Posteriormente, la función `count` contabiliza únicamente aquellos pares que satisfacen simultáneamente:

$$  
a(i)\ge0  
$$

$$  
a(i)=a(j)  
$$

$$  
solapan(c_i,c_j)  
$$

Por definición, estos son exactamente los choques solicitados por el problema.

---

## 3. Función `capacidadFallida`

### Implementación

```scala
def capacidadFallida(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
  cursos.indices.count { i =>
    a(i) >= 0 && capAula(aulas(a(i))) < estCurso(cursos(i))
  }
```

### Especificación

Debe contar la cantidad de cursos cuya aula asignada no posee capacidad suficiente.

Formalmente:

$$
CF =
\left|
\left\{
i
\mid
capacidad(aula_i)
<
estudiantes(curso_i)
\right\}
\right|
$$

### Argumento de corrección

La función recorre todos los cursos y verifica la condición:

$$  
capacidad(aula_i)  
<  
estudiantes(curso_i)  
$$

La operación `count` incrementa el resultado exactamente cuando dicha condición es verdadera.

Por lo tanto, el resultado coincide con la definición matemática de capacidad fallida.

---

## 4. Función `desperdicio`

### Implementación

```scala
def desperdicio(cursos: Cursos, aulas: Aulas, a: Asignacion): Int =
  cursos.indices.foldLeft(0) { (acc, i) =>
    if (a(i) >= 0 && capAula(aulas(a(i))) >= estCurso(cursos(i)))
      acc + (capAula(aulas(a(i))) - estCurso(cursos(i)))
    else acc
  }
```

### Especificación

Debe calcular:

$$  
DE=  
\sum  
(capacidad-estudiantes)  
$$

sobre todos los cursos asignados cuya aula tenga capacidad suficiente.

### Invariante

Después de procesar los primeros $k$ cursos, el acumulador contiene:

$$  
\sum_{i=0}^{k-1}  
(capacidad_i-estudiantes_i)  
$$

considerando únicamente aquellos cursos que cumplen:

$$  
capacidad_i \ge estudiantes_i  
$$

### Caso base

La ejecución inicia con:

```scala
foldLeft(0)
```

Por tanto:

$$  
DE = 0  
$$

que corresponde a la suma vacía.

### Paso inductivo

Al procesar un nuevo curso:

- Si

$$  
capacidad_i \ge estudiantes_i  
$$

se agrega:

$$  
capacidad_i-estudiantes_i  
$$

al acumulador.

- En caso contrario, el acumulador permanece inalterado.

Por inducción sobre la cantidad de cursos procesados, el resultado final corresponde exactamente al desperdicio definido en el enunciado.

---

## 5. Función `movilidad`

### Implementación

```scala
def movilidad(...)
```

### Especificación

Los cursos asignados deben ordenarse cronológicamente y luego sumarse las distancias entre las aulas de cursos consecutivos.

### Ordenamiento

La instrucción:

```scala
sortBy(i => iniCurso(cursos(i)))
```

produce una secuencia:

$$  
c_1,c_2,\ldots,c_n  
$$

ordenada por hora de inicio.

### Función auxiliar

La recursión utiliza el patrón:

```scala
case i1 +: i2 +: resto =>
```

que selecciona los dos primeros cursos consecutivos.

Posteriormente calcula:

$$  
D(a(i_1),a(i_2))  
$$

y continúa recursivamente con:

$$  
(i_2,\ldots,i_n)  
$$

### Caso base

Cuando quedan cero o un curso:

```scala
case _ => 0
```

No existe desplazamiento entre aulas.

Por tanto:

$$  
MV=0  
$$

### Paso inductivo

Supóngase una secuencia:

$$  
(i_1,i_2,\ldots,i_n)  
$$

La función calcula:

$$
D(i_1,i_2)  

MV(i_2,\ldots,i_n)  
$$

Por hipótesis inductiva, la llamada recursiva calcula correctamente todas las distancias restantes.

Por lo tanto:

$$  
MV=  
\sum_{k=1}^{n-1}  
D(i_k,i_{k+1})  
$$

que coincide exactamente con la definición de movilidad.

---

## 6. Función `costoAsignacion`

### Implementación

```scala
def costoAsignacion(...)
```

### Especificación

Debe calcular:

$$
Costo = w_{CH} \cdot CH + w_{CF} \cdot CF + w_{DE} \cdot DE + w_{MV} \cdot MV
$$

### Argumento de corrección

Las funciones:

- `choques`
- `capacidadFallida`
- `desperdicio`
- `movilidad`

calculan correctamente cada componente de la función objetivo.

Posteriormente se multiplican por sus respectivos pesos y se suman.

Por definición:

$$  
Costo =  
w_{CH}CH+w_{CF}CF+w_{DE}DE+w_{MV}MV  
$$

Por lo tanto, el resultado coincide exactamente con el costo total especificado.

---

## 7. Función `generarAsignaciones`

### Implementación

```scala
def generarAsignaciones(n,m)
```

### Especificación

Debe generar todas las asignaciones posibles:

$$  
{0,\ldots,m-1}^{n}  
$$

### Caso base

Cuando:

```scala
n == 0
```

la función retorna:

```scala
Vector(Vector())
```

Existe exactamente una asignación para cero cursos:

$$  
()  
$$

Por lo tanto, el caso base es correcto.

### Hipótesis inductiva

Supongamos que:

```scala
generarAsignaciones(n-1,m)
```

genera correctamente todas las asignaciones posibles de longitud:

$$  
n-1  
$$

### Paso inductivo

Para cada aula:

$$  
a \in {0,\ldots,m-1}  
$$

la instrucción:

```scala
aula +: asig
```

agrega dicha aula al inicio de cada asignación parcial.

De esta forma se generan todas las asignaciones posibles de longitud:

$$  
n  
$$

La cantidad total obtenida es:

$$
m \cdot m^{n-1} = m^n
$$

que coincide con el número esperado de combinaciones.

Por inducción matemática, la función genera exactamente todas las asignaciones posibles.

---

## 8. Función `asignacionOptima`

### Implementación

```scala
def asignacionOptima(...)
```

### Especificación

Debe retornar una pareja:

$$
(a^*, c^*)
$$

tal que:

$$
c^* = \min_a CT(a)
$$

y

$$
a^* = \operatorname*{arg\,min}_{a} CT(a)
$$

### Invariante del `foldLeft`

Después de procesar las primeras $k$ asignaciones, la variable:

```scala
mejor
```

contiene la asignación de menor costo encontrada hasta ese momento.

### Caso base

Inicialmente:

```scala
(Vector(), Int.MaxValue)
```

representa una solución ficticia con costo infinito.

Formalmente:

$$  
Costo(mejor)=+\infty  
$$

Cualquier asignación real tendrá un costo menor.

### Paso inductivo

Para cada asignación:

```scala
actual
```

se calcula:

$$  
Costo(actual)  
$$

Si:

$$  
Costo(actual)  
<  
Costo(mejor)  
$$

entonces la mejor solución se actualiza.

En caso contrario se conserva la mejor solución encontrada hasta el momento.

### Correctitud

La función recorre todas las asignaciones generadas por `generarAsignaciones`.

Como el invariante garantiza que siempre se conserva la asignación de menor costo observada, al finalizar el recorrido se obtiene:

$$  
a^*
\operatorname*{arg,min}_{a}  
Costo(a)  
$$

junto con su costo asociado:

$$  
c^*
\min_a Costo(a)  
$$

Por lo tanto, la función retorna correctamente la asignación óptima.

---

## 9. Función `choquesPar`

### Implementación

```scala
def choquesPar(cursos: Cursos, a: Asignacion): Int = {
  val n    = cursos.length
  val mitad = n / 2
  val (izq, der) = parallel(
    (for {
      i <- 0 until mitad
      j <- (i + 1) until mitad
      if a(i) >= 0 && a(i) == a(j) && solapan(cursos(i), cursos(j))
    } yield 1).sum,
    (for {
      i <- mitad until n
      j <- (i + 1) until n
      if a(i) >= 0 && a(i) == a(j) && solapan(cursos(i), cursos(j))
    } yield 1).sum
  )
  val frontera = (for {
    i <- 0 until mitad
    j <- mitad until n
    if a(i) >= 0 && a(i) == a(j) && solapan(cursos(i), cursos(j))
  } yield 1).sum
  izq + der + frontera
}
```

### Especificación

Debe calcular el mismo valor que `choques`:

$$
CH =
\left|
\left\{
(i,j)
\mid
i < j,\;
a(i) = a(j) \ge 0,\;
solapan(c_i, c_j)
\right\}
\right|
$$

### Argumento de corrección

Sea $mitad = \lfloor n/2 \rfloor$. El conjunto de todos los pares $(i,j)$ con $i < j$ se puede
particionar en tres subconjuntos disjuntos y exhaustivos:

$$
P_{izq} = \{(i,j) \mid 0 \le i < j < mitad\}
$$

$$
P_{der} = \{(i,j) \mid mitad \le i < j < n\}
$$

$$
P_{front} = \{(i,j) \mid 0 \le i < mitad \le j < n\}
$$

Se verifica que:

$$
P_{izq} \cup P_{der} \cup P_{front}
= \{(i,j) \mid 0 \le i < j < n\}
$$

y que los tres conjuntos son mutuamente disjuntos, ya que las condiciones sobre los
rangos de $i$ y $j$ son incompatibles entre sí.

Cada uno de los tres subconjuntos aplica exactamente las mismas condiciones de conteo
que `choques`. Por tanto:

$$
CH = CH_{izq} + CH_{der} + CH_{front}
$$

Los términos $CH_{izq}$ y $CH_{der}$ se calculan en paralelo sin interferencia, ya que
operan sobre rangos de índices disjuntos y no modifican estado compartido.
$CH_{front}$ se calcula secuencialmente una vez que ambas tareas paralelas terminan.

Por lo tanto, `choquesPar` produce el mismo resultado que `choques`.

---

## 10. Función `desperdicioPar`

### Implementación

```scala
def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
  val n    = cursos.length
  val mitad = n / 2
  def desperdicioRango(inicio: Int, fin: Int): Int =
    (inicio until fin).map { i =>
      if (a(i) >= 0) {
        val cap = capAula(aulas(a(i)))
        val est = estCurso(cursos(i))
        if (cap >= est) cap - est else 0
      } else 0
    }.sum
  val (izq, der) = parallel(
    desperdicioRango(0, mitad),
    desperdicioRango(mitad, n)
  )
  izq + der
}
```

### Especificación

Debe calcular el mismo valor que `desperdicio`:

$$
DE =
\sum_{\substack{i=0 \\ a(i) \ge 0 \\ cap_i \ge est_i}}^{n-1}
(cap_i - est_i)
$$

### Argumento de corrección

La suma total sobre $n$ cursos puede descomponerse como:

$$
DE
=
\sum_{i=0}^{mitad-1} f(i)+ \sum_{i=mitad}^{n-1} f(i)
$$

donde:

$$ 
f(i) =
\begin{cases}
cap_i - est_i & \text{si } a(i) \ge 0 \text{ y } cap_i \ge est_i \\
0 & \text{en otro caso}
\end{cases}
$$

Cada término $f(i)$ depende únicamente del curso $i$ y no de ningún otro curso. Por
tanto, los rangos $[0, mitad)$ y $[mitad, n)$ son completamente independientes entre
sí y pueden evaluarse en paralelo sin riesgo de condición de carrera.

La función `desperdicioRango` aplica exactamente la misma lógica que `desperdicio`
sobre el subrango indicado.

Por la propiedad asociativa de la suma:

$$
DE = DE_{izq} + DE_{der}
$$

Por lo tanto, `desperdicioPar` produce el mismo resultado que `desperdicio`.

---

## 11. Función `movilidadPar`

### Implementación

```scala
def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                 a: Asignacion): Int = {
  val ordenados =
    (0 until cursos.length)
      .filter(i => a(i) >= 0)
      .sortBy(i => iniCurso(cursos(i)))
      .toVector
  def auxiliar(inicio: Int, fin: Int): Int =
    if (fin - inicio <= 1) 0
    else if (fin - inicio == 2)
      d(a(ordenados(inicio)))(a(ordenados(inicio + 1)))
    else {
      val mitad = inicio + (fin - inicio) / 2
      val (izq, der) = parallel(
        auxiliar(inicio, mitad),
        auxiliar(mitad, fin)
      )
      val frontera = d(a(ordenados(mitad - 1)))(a(ordenados(mitad)))
      izq + der + frontera
    }
  auxiliar(0, ordenados.length)
}
```

### Especificación

Debe calcular el mismo valor que `movilidad`:

$$
MV =
\sum_{k=0}^{K-2}
D\bigl(a(\sigma_k),\, a(\sigma_{k+1})\bigr)
$$

donde $\sigma_0, \sigma_1, \ldots, \sigma_{K-1}$ es la secuencia de cursos asignados
ordenados por hora de inicio.

### Argumento de corrección

Sea $\sigma$ la secuencia ordenada de índices de cursos asignados con longitud $K$.
La función `auxiliar(inicio, fin)` calcula:

$$
\sum_{k=inicio}^{fin-2}
D\bigl(a(\sigma_k),\, a(\sigma_{k+1})\bigr)
$$

**Caso base** $fin - inicio \le 1$: no existe ningún par consecutivo, por tanto:

$$
MV = 0
$$

**Caso base** $fin - inicio = 2$: existe exactamente un par:

$$
MV = D(a(\sigma_{inicio}),\, a(\sigma_{inicio+1}))
$$

**Paso inductivo**: sea $mitad = inicio + \lfloor(fin - inicio)/2\rfloor$.
Las distancias de la secuencia se pueden descomponer en:

$$
\sum_{k=inicio}^{fin-2} D_k
=
\underbrace{\sum_{k=inicio}^{mitad-2} D_k}_{auxiliar(inicio,\, mitad)}+\underbrace{D(a(\sigma_{mitad-1}),\, a(\sigma_{mitad}))}_{frontera}+ \underbrace{\sum_{k=mitad}^{fin-2} D_k}_{auxiliar(mitad,\, fin)}
$$

El término de frontera conecta ambas mitades y se calcula secuencialmente. Los dos
subproblemas operan sobre rangos disjuntos de $\sigma$ y no comparten estado, por
tanto pueden evaluarse en paralelo sin condición de carrera.

Por hipótesis inductiva, cada llamada recursiva calcula correctamente la suma de su
subrango. Por lo tanto, `auxiliar(0, K)` calcula correctamente $MV$.

---

## 12. Función `generarAsignacionesPar`

### Implementación

```scala
def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
  if (n == 0)
    Vector(Vector.empty)
  else {
    val mitad = m / 2
    def subArbol(v: Int): Vector[Asignacion] =
      generarAsignaciones(n - 1, m).map(resto => v +: resto)
    val (izq, der) = parallel(
      (0 until mitad).toVector.flatMap(subArbol),
      (mitad until m).toVector.flatMap(subArbol)
    )
    izq ++ der
  }
}
```

### Especificación

Debe generar el mismo conjunto que `generarAsignaciones`:

$$
\{0,\ldots,m-1\}^n
$$

con cardinalidad $m^n$.

### Argumento de corrección

**Caso base** $n = 0$: retorna `Vector(Vector.empty)`, que es la única asignación
posible para cero cursos. Coincide con `generarAsignaciones(0, m)`.

**Caso recursivo**: los valores del primer curso se dividen en:

$$
I_{izq} = \{0,\ldots, mitad-1\}
\qquad
I_{der} = \{mitad,\ldots, m-1\}
$$

Estos dos conjuntos son disjuntos y su unión es $\{0,\ldots,m-1\}$, por tanto la
partición es exhaustiva.

Para cada valor $v \in \{0,\ldots,m-1\}$, la función `subArbol(v)` genera todas las
asignaciones de la forma $v \mathbin{+:} resto$ donde $resto \in \{0,\ldots,m-1\}^{n-1}$.
Esto produce exactamente $m^{n-1}$ asignaciones distintas por valor de $v$.

Las llamadas a `subArbol` para distintos valores de $v$ son independientes entre sí:
cada una invoca `generarAsignaciones` (secuencial) sobre los mismos parámetros de solo
lectura y construye vectores nuevos sin modificar estado compartido. Por tanto pueden
ejecutarse en paralelo sin condición de carrera.

La cantidad total de asignaciones producidas es:

$$
m \cdot m^{n-1} = m^n
$$

y cada asignación es única porque el primer componente distingue los grupos. Por lo
tanto, `generarAsignacionesPar` produce exactamente el mismo conjunto que
`generarAsignaciones`.

---

## 13. Función `asignacionOptimaPar`

### Implementación

```scala
def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                        w: Pesos): (Asignacion, Int) = {
  val candidatas = generarAsignacionesPar(cursos.length, aulas.length)
  val mitad      = candidatas.length / 2
  def minimoEnRango(inicio: Int, fin: Int): (Asignacion, Int) =
    (inicio until fin).foldLeft(
      (candidatas(inicio),
       costoAsignacion(cursos, aulas, d, candidatas(inicio), w))
    ) {
      case (mejor @ (_, costoMejor), i) =>
        val costo = costoAsignacion(cursos, aulas, d, candidatas(i), w)
        if (costo < costoMejor) (candidatas(i), costo) else mejor
    }
  val (minIzq, minDer) = parallel(
    minimoEnRango(0, mitad),
    minimoEnRango(mitad, candidatas.length)
  )
  if (minIzq._2 <= minDer._2) minIzq else minDer
}
```

### Especificación

Debe retornar la misma pareja óptima que `asignacionOptima`:

$$
\bigl(a^*,\, c^*\bigr)
\quad\text{tal que}\quad
c^* = \min_{a} CT(a)
\quad\text{y}\quad
a^* = \operatorname*{arg\,min}_{a} CT(a)
$$

### Argumento de corrección

Sea $C$ el vector de todas las candidatas con $|C| = m^n$.

**Corrección de `minimoEnRango`**: la función recorre el subrango $[inicio, fin)$
con un `foldLeft` cuyo invariante es: después de procesar los primeros $k$ elementos
del rango, el acumulador contiene la asignación de menor costo vista hasta ese momento.

- **Caso base**: el acumulador se inicializa con `candidatas(inicio)` y su costo real,
  por lo que el invariante se cumple trivialmente.
- **Paso inductivo**: al procesar el elemento $i$, si su costo es menor que el del
  acumulador actual, el acumulador se actualiza; en caso contrario se conserva. El
  invariante se mantiene.

Al finalizar, `minimoEnRango(inicio, fin)` retorna el mínimo del subrango
$C[inicio..fin)$.

**Corrección de la combinación**: el mínimo global satisface:

$$
\min_{a \in C} CT(a)
=
\min\!\Bigl(
\min_{a \in C[0..mitad)}\! CT(a),\;
\min_{a \in C[mitad..|C|)}\! CT(a)
\Bigr)
$$

Los dos subrangos son disjuntos y su unión es $C$ completo, por lo que la igualdad
es exacta.

Cada llamada a `minimoEnRango` opera únicamente sobre su subrango de solo lectura
y no modifica estado compartido. Por tanto pueden ejecutarse en paralelo sin condición
de carrera.

La comparación final `if (minIzq._2 <= minDer._2) minIzq else minDer` selecciona
correctamente el mínimo global.

Por lo tanto, `asignacionOptimaPar` produce el mismo resultado que `asignacionOptima`.
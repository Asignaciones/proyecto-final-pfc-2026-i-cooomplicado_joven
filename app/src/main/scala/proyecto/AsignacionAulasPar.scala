package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = {
    val n = cursos.length
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

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = {
    val n = cursos.length
    val mitad = n / 2

    def desperdicioRango(inicio: Int, fin: Int): Int =
      (inicio until fin).map { i =>
        if (a(i) >= 0) {
          val capacidad   = capAula(aulas(a(i)))
          val estudiantes = estCurso(cursos(i))
          if (capacidad >= estudiantes) capacidad - estudiantes else 0
        } else 0
      }.sum

    val (izq, der) = parallel(
      desperdicioRango(0, mitad),
      desperdicioRango(mitad, n)
    )

    izq + der
  }

  /** Versión paralela de movilidad: divide el vector de cursos en dos mitades. */
  def movilidadPar(cursos: Cursos, aulas: Aulas, d: Distancias, a: Asignacion): Int = {
    val cursosOrdenados =
      (0 until cursos.length)
        .filter(i => a(i) >= 0).sortBy(i => iniCurso(cursos(i))).toVector

    def auxiliar(inicio: Int, fin: Int): Int = {
      if (fin - inicio <= 1)
        0
      else if (fin - inicio == 2)
        d(a(cursosOrdenados(inicio)))(a(cursosOrdenados(inicio + 1)))
      else {
        val mitad = inicio + (fin - inicio) / 2
        val (izq, der) =
          parallel(
            auxiliar(inicio, mitad),
            auxiliar(mitad, fin)
          )
        val frontera =
          d(a(cursosOrdenados(mitad - 1)))(a(cursosOrdenados(mitad)))
        izq + der + frontera
      }
    }
    auxiliar(0, cursosOrdenados.length)
  }

  /**
   * Versión paralela de generarAsignaciones:
   * paraleliza la construcción usando parallel sobre los valores del primer curso.
   */
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = {
    if (n == 0)
      Vector(Vector.empty)
    else {
      val mitad = m / 2

      def subArbol(primerValor: Int): Vector[Asignacion] =
        generarAsignaciones(n - 1, m).map(resto => primerValor +: resto)

      val (izq, der) = parallel(
        (0 until mitad).toVector.flatMap(subArbol),
        (mitad until m).toVector.flatMap(subArbol)
      )

      izq ++ der
    }
  }

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = {
    val candidatas = generarAsignacionesPar(cursos.length, aulas.length)
    val mitad      = candidatas.length / 2

    def minimoEnRango(inicio: Int, fin: Int): (Asignacion, Int) =
      (inicio until fin).foldLeft((candidatas(inicio),
        costoAsignacion(cursos, aulas, d, candidatas(inicio), w))) {
        case (mejorActual @ (_, costoActual), i) =>
          val costo = costoAsignacion(cursos, aulas, d, candidatas(i), w)
          if (costo < costoActual) (candidatas(i), costo) else mejorActual
      }

    val (minIzq, minDer) = parallel(
      minimoEnRango(0, mitad),
      minimoEnRango(mitad, candidatas.length)
    )

    if (minIzq._2 <= minDer._2) minIzq else minDer
  }
}
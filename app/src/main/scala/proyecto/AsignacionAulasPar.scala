package proyecto

import common._
import AsignacionAulas._

object AsignacionAulasPar {

  /** Versión paralela de choques: divide el vector de cursos en dos mitades. */
  def choquesPar(cursos: Cursos, a: Asignacion): Int = ???

  /** Versión paralela de desperdicio: divide el vector de cursos en dos mitades. */
  def desperdicioPar(cursos: Cursos, aulas: Aulas, a: Asignacion): Int = ???

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
  def generarAsignacionesPar(n: Int, m: Int): Vector[Asignacion] = ???

  /**
   * Versión paralela de asignacionOptima:
   * divide el espacio de candidatos en dos mitades y combina los mínimos.
   */
  def asignacionOptimaPar(cursos: Cursos, aulas: Aulas, d: Distancias,
                          w: Pesos): (Asignacion, Int) = ???
}

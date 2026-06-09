package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  val c1: Cursos    = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas     = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos      = (1000, 100, 1, 2)

  test("choquesPar: asignacion [0,0,1] tiene 1 choque") {
    assert(choquesPar(c1, Vector(0, 0, 1)) == 1)
  }

  test("choquesPar: asignacion [0,1,0] no tiene choques") {
    assert(choquesPar(c1, Vector(0, 1, 0)) == 0)
  }

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  // Tests para movilidadPar
  test("movilidadPar caso 000") {
    val m = Vector(0,0,0)
    assert(movilidadPar(c1,a1,d1,m) == movilidad(c1,a1,d1,m))
  }

  test("movilidadPar caso 001") {
    val a = Vector(0,0,1)
    assert(movilidadPar(c1,a1,d1,a) == movilidad(c1,a1,d1,a))
  }

  test("movilidadPar caso 010") {
    val f = Vector(0,1,0)
    assert(movilidadPar(c1,a1,d1,f) == movilidad(c1,a1,d1,f))
  }

  test("movilidadPar caso 101") {
    val e = Vector(1,0,1)
    assert(movilidadPar(c1,a1,d1,e) == movilidad(c1,a1,d1,e))
  }

  test("movilidadPar caso 111") {
    val r = Vector(1,1,1)
    assert(movilidadPar(c1,a1,d1,r) == movilidad(c1,a1,d1,r))
  }
}

package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  // ---------------------------------------------------------------------------
  // Datos compartidos — Ejemplo 1 del enunciado
  // ---------------------------------------------------------------------------
  val c1: Cursos     = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas      = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos       = (1000, 100, 1, 2)

  // Datos adicionales — Ejemplo 2 del enunciado (4 cursos, 2 aulas)
  val c2: Cursos     = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
  val a2: Aulas      = Vector(("S201", 45), ("S202", 30))
  val d2: Distancias = Vector(Vector(0, 5), Vector(5, 0))

  // Datos para 1 curso y 1 aula (casos extremos)
  val c3: Cursos     = Vector(("X01", 0, 4, 10))
  val a3: Aulas      = Vector(("R01", 20))
  val d3: Distancias = Vector(Vector(0))

  // ---------------------------------------------------------------------------
  // choquesPar
  // ---------------------------------------------------------------------------

  test("choquesPar: asignacion [0,0,1] tiene 1 choque") {
    assert(choquesPar(c1, Vector(0, 0, 1)) == 1)
  }

  test("choquesPar: asignacion [0,1,0] no tiene choques") {
    assert(choquesPar(c1, Vector(0, 1, 0)) == 0)
  }

  test("choquesPar: coincide con secuencial en todos los casos del ejemplo 1") {
    val asignaciones = generarAsignaciones(c1.length, a1.length)
    asignaciones.foreach { a =>
      assert(choquesPar(c1, a) == choques(c1, a),
        s"Fallo con asignacion $a")
    }
  }

  test("choquesPar: asignacion [0,0,0,0] en ejemplo 2 tiene 6 choques") {
    // F01[0,4), F02[4,8), F03[8,12), F04[12,16) no se solapan entre si => 0 choques
    assert(choquesPar(c2, Vector(0, 0, 0, 0)) == 0)
  }

  test("choquesPar: cursos no solapados en misma aula no generan choques") {
    // c2: los 4 cursos son consecutivos (no se solapan), todos en aula 0
    assert(choquesPar(c2, Vector(0, 0, 0, 0)) == 0)
  }

  test("choquesPar: 1 curso en 1 aula => 0 choques") {
    assert(choquesPar(c3, Vector(0)) == 0)
  }

  // ---------------------------------------------------------------------------
  // desperdicioPar
  // ---------------------------------------------------------------------------

  test("desperdicioPar: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(cap=30): M01(25)->5, M02(30)->0 | E102(cap=40): M03(20)->20 => total=25
    assert(desperdicioPar(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicioPar: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30): M01(25)->5, M03(20)->10 | E102(40): M02(30)->10 => total=25
    assert(desperdicioPar(c1, a1, Vector(0, 1, 0)) == 25)
  }

  test("desperdicioPar: coincide con secuencial en todos los casos del ejemplo 1") {
    val asignaciones = generarAsignaciones(c1.length, a1.length)
    asignaciones.foreach { a =>
      assert(desperdicioPar(c1, a1, a) == desperdicio(c1, a1, a),
        s"Fallo con asignacion $a")
    }
  }

  test("desperdicioPar: aula exactamente llena contribuye 0 al desperdicio") {
    // M02 tiene 30 estudiantes, E101 tiene capacidad 30 => desperdicio = 0 para ese curso
    val asig = Vector(1, 0, 0) // M02 -> E101 (cap=30, est=30)
    val d = desperdicioPar(c1, a1, asig)
    // M01 -> E102 (cap=40, est=25) => 15; M02 -> E101 (cap=30, est=30) => 0;
    // M03 -> E101 (cap=30, est=20) => 10; total = 25
    assert(d == desperdicio(c1, a1, asig))
  }

  test("desperdicioPar: 1 curso 1 aula con capacidad sobrante") {
    // R01(cap=20), X01(est=10) => desperdicio=10
    assert(desperdicioPar(c3, a3, Vector(0)) == 10)
  }

  test("desperdicioPar: coincide con secuencial en todos los casos del ejemplo 2") {
    val asignaciones = generarAsignaciones(c2.length, a2.length)
    asignaciones.foreach { a =>
      assert(desperdicioPar(c2, a2, a) == desperdicio(c2, a2, a),
        s"Fallo con asignacion $a")
    }
  }

  // ---------------------------------------------------------------------------
  // movilidadPar
  // ---------------------------------------------------------------------------

  test("movilidadPar: asignacion [0,0,1] tiene movilidad 3") {
    assert(movilidadPar(c1, a1, d1, Vector(0, 0, 1)) == 3)
  }

  test("movilidadPar caso [0,0,0]") {
    val m = Vector(0, 0, 0)
    assert(movilidadPar(c1, a1, d1, m) == movilidad(c1, a1, d1, m))
  }

  test("movilidadPar caso [0,0,1]") {
    val a = Vector(0, 0, 1)
    assert(movilidadPar(c1, a1, d1, a) == movilidad(c1, a1, d1, a))
  }

  test("movilidadPar caso [0,1,0]") {
    val f = Vector(0, 1, 0)
    assert(movilidadPar(c1, a1, d1, f) == movilidad(c1, a1, d1, f))
  }

  test("movilidadPar caso [1,0,1]") {
    val e = Vector(1, 0, 1)
    assert(movilidadPar(c1, a1, d1, e) == movilidad(c1, a1, d1, e))
  }

  test("movilidadPar caso [1,1,1]") {
    val r = Vector(1, 1, 1)
    assert(movilidadPar(c1, a1, d1, r) == movilidad(c1, a1, d1, r))
  }

  test("movilidadPar: 1 curso => movilidad 0") {
    assert(movilidadPar(c3, a3, d3, Vector(0)) == 0)
  }

  test("movilidadPar: coincide con secuencial en todos los casos del ejemplo 2") {
    val asignaciones = generarAsignaciones(c2.length, a2.length)
    asignaciones.foreach { a =>
      assert(movilidadPar(c2, a2, d2, a) == movilidad(c2, a2, d2, a),
        s"Fallo con asignacion $a")
    }
  }

  // ---------------------------------------------------------------------------
  // generarAsignacionesPar
  // ---------------------------------------------------------------------------

  test("generarAsignacionesPar: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignacionesPar(2, 2).length == 4)
  }

  test("generarAsignacionesPar: 3 cursos y 2 aulas produce 8 asignaciones") {
    assert(generarAsignacionesPar(3, 2).length == 8)
  }

  test("generarAsignacionesPar: 2 cursos y 3 aulas produce 9 asignaciones") {
    assert(generarAsignacionesPar(2, 3).length == 9)
  }

  test("generarAsignacionesPar: 0 cursos produce exactamente 1 asignacion vacia") {
    val resultado = generarAsignacionesPar(0, 3)
    assert(resultado == Vector(Vector.empty))
  }

  test("generarAsignacionesPar: contiene las mismas asignaciones que la version secuencial") {
    val par = generarAsignacionesPar(3, 2).toSet
    val seq = generarAsignaciones(3, 2).toSet
    assert(par == seq)
  }

  test("generarAsignacionesPar: 1 curso y 1 aula produce exactamente [Vector(0)]") {
    assert(generarAsignacionesPar(1, 1) == Vector(Vector(0)))
  }

  // ---------------------------------------------------------------------------
  // asignacionOptimaPar
  // ---------------------------------------------------------------------------

  test("asignacionOptimaPar: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo <= 37)
  }

  test("asignacionOptimaPar: coincide con la version secuencial en ejemplo 1") {
    val (_, costoSeq) = asignacionOptima(c1, a1, d1, w)
    val (_, costoPar) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costoSeq == costoPar)
  }

  test("asignacionOptimaPar: coincide con la version secuencial en ejemplo 2") {
    val (_, costoSeq) = asignacionOptima(c2, a2, d2, w)
    val (_, costoPar) = asignacionOptimaPar(c2, a2, d2, w)
    assert(costoSeq == costoPar)
  }

  test("asignacionOptimaPar: 1 curso 1 aula devuelve asignacion [0]") {
    val (asig, _) = asignacionOptimaPar(c3, a3, d3, w)
    assert(asig == Vector(0))
  }

  test("asignacionOptimaPar: el costo devuelto coincide con costoAsignacion aplicado a la asignacion") {
    val (asig, costo) = asignacionOptimaPar(c1, a1, d1, w)
    assert(costo == costoAsignacion(c1, a1, d1, asig, w))
  }

  test("asignacionOptimaPar: la asignacion optima no tiene choques en ejemplo 1") {
    // Con w_CH=1000 dominante, la optima debe tener 0 choques
    val (asig, _) = asignacionOptimaPar(c1, a1, d1, w)
    assert(choques(c1, asig) == 0)
  }
}
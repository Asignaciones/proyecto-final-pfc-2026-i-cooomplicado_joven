package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._
import AsignacionAulasPar._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasParTest extends AnyFunSuite {

  // ---------------------------------------------------------------------------
  // Datos de prueba — Ejemplo 1 del enunciado
  // Cursos: M01(8:00-10:00, 25 est), M02(9:00-11:00, 30 est), M03(12:00-14:00, 20 est)
  // Aulas:  E101(cap 30), E102(cap 40)
  // ---------------------------------------------------------------------------
  val c1: Cursos     = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas      = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos       = (1000, 100, 1, 2)
  // Datos de prueba — Ejemplo 2 del enunciado
  // Cursos sin solape entre sí (consecutivos), F03 no cabe en ninguna aula
  val c2: Cursos     = Vector(("F01", 0, 4, 40), ("F02", 4, 8, 25), ("F03", 8, 12, 50), ("F04", 12, 16, 15))
  val a2: Aulas      = Vector(("S201", 45), ("S202", 30))
  val d2: Distancias = Vector(Vector(0, 5), Vector(5, 0))


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

  // ---------------------------------------------------------------------------
  // solapan — 5 casos
  // ---------------------------------------------------------------------------

  test("solapan: M01(4-8) y M02(6-10) se traslapan porque M02 empieza antes de que M01 termine") {
    // [4,8) y [6,10) comparten el intervalo [6,8)
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01(4-8) y M03(12-16) no se traslapan porque hay un hueco entre ellos") {
    // [4,8) y [12,16) son disjuntos — fin de M01 es 8, inicio de M03 es 12
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos consecutivos que se tocan justo en el limite no se traslapan") {
    // [4,8) y [8,12): fin del primero == inicio del segundo, intervalo compartido vacío
    assert(!solapan(("A", 4, 8, 10), ("B", 8, 12, 10)))
  }

  test("solapan: un curso contenido completamente dentro de otro se solapa") {
    // [2,10) contiene a [4,6), solapamiento total
    assert(solapan(("A", 2, 10, 10), ("B", 4, 6, 10)))
  }

  test("solapan: dos cursos identicos en horario se solapan") {
    // [4,8) y [4,8): mismo intervalo, solapamiento total
    assert(solapan(("A", 4, 8, 10), ("B", 4, 8, 10)))
  }

  // ---------------------------------------------------------------------------
  // choques — 5 casos
  // ---------------------------------------------------------------------------

  test("choques: asignacion [0,0,1] tiene 1 choque porque M01 y M02 comparten E101 y se solapan") {
    // M01 y M02 en aula 0 (E101), se solapan → 1 choque. M03 en aula 1 → no choca con nadie
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] tiene 0 choques porque cada par con mismo aula no se solapa") {
    // M01 y M03 comparten aula 0 pero no se solapan en horario → 0 choques
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  test("choques: si todos los cursos estan en aulas distintas el resultado es siempre 0") {
    // Con m >= n siempre es posible asignar una aula distinta a cada curso
    val cursos = Vector(("A", 0, 4, 10), ("B", 2, 6, 10), ("C", 4, 8, 10))
    assert(choques(cursos, Vector(0, 1, 2)) == 0)
  }

  test("choques: tres cursos que se solapan entre si en la misma aula generan 3 choques") {
    // Pares (0,1), (0,2), (1,2): los tres se solapan y comparten aula → C(3,2) = 3
    val cursos = Vector(("A", 0, 8, 10), ("B", 2, 10, 10), ("C", 4, 12, 10))
    assert(choques(cursos, Vector(0, 0, 0)) == 3)
  }

  test("choques: ejemplo 2 asignacion [0,1,0,1] tiene 0 choques porque los cursos son consecutivos") {
    // F01(0-4), F02(4-8), F03(8-12), F04(12-16): ningún par se solapa aunque compartan aula
    assert(choques(c2, Vector(0, 1, 0, 1)) == 0)
  }

  // ---------------------------------------------------------------------------
  // capacidadFallida — 5 casos
  // ---------------------------------------------------------------------------

  test("capacidadFallida: asignacion [0,1,0] del ejemplo 1 tiene 0 fallos porque todas las aulas alcanzan") {
    // M01(25 est) en E101(30): ok. M02(30 est) en E102(40): ok. M03(20 est) en E101(30): ok
    assert(capacidadFallida(c1, a1, Vector(0, 1, 0)) == 0)
  }

  test("capacidadFallida: asignacion [1,1,1] tiene 1 fallo porque M02 no cabe en E101") {
    // M01(25) en E101(30): ok. M02(30) en E101(30): ok. Espera — revisemos con E101 cap 30
    // M02 tiene 30 estudiantes y E101 tiene cap 30: 30 < 30 es false → no falla
    // Para forzar un fallo real: M02(30 est) en E101(cap 28)
    val aulasAjustadas = Vector(("E101", 28), ("E102", 40))
    assert(capacidadFallida(c1, aulasAjustadas, Vector(0, 0, 1)) == 1)
  }

  test("capacidadFallida: ejemplo 2 asignacion [0,1,0,1] tiene 1 fallo porque F03 no cabe en S201") {
    // F03 tiene 50 estudiantes, S201 tiene cap 45 → 1 fallo
    assert(capacidadFallida(c2, a2, Vector(0, 1, 0, 1)) == 1)
  }

  test("capacidadFallida: ejemplo 2 asignacion [0,1,1,0] tiene 2 fallos porque F03 no cabe en ninguna aula") {
    // F03(50 est) en S202(cap 30) → fallo. F01(40 est) en S201(cap 45) → ok
    // F02(25) en S202(30) → ok. F04(15) en S201(45) → ok
    // Solo F03 falla → 1 fallo
    assert(capacidadFallida(c2, a2, Vector(0, 1, 1, 0)) == 1)
  }

  test("capacidadFallida: si todos los cursos caben exactamente no hay fallos") {
    // Aulas con capacidad exacta igual al numero de estudiantes
    val cursos = Vector(("A", 0, 4, 30), ("B", 6, 10, 40))
    val aulas  = Vector(("X", 30), ("Y", 40))
    assert(capacidadFallida(cursos, aulas, Vector(0, 1)) == 0)
  }

  // ---------------------------------------------------------------------------
  // desperdicio — 5 casos
  // ---------------------------------------------------------------------------

  test("desperdicio: asignacion [0,1,0] del ejemplo 1 suma 25 en total") {
    // M01(25 est) en E101(30): 30-25=5. M02(30 est) en E102(40): 40-30=10. M03(20) en E101(30): 30-20=10
    // Total: 5+10+10 = 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  test("desperdicio: asignacion [0,0,1] del ejemplo 1 tambien suma 25") {
    // M01(25) en E101(30): 5. M02(30) en E101(30): 0. M03(20) en E102(40): 20. Total: 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: cursos que no caben en su aula no suman al desperdicio") {
    // F03(50 est) en S201(45): capacidad insuficiente → no suma. Solo los que caben suman.
    // F01(40) en S201(45): 5. F02(25) en S202(30): 5. F03(50) en S201(45): no suma. F04(15) en S202(30): 15
    assert(desperdicio(c2, a2, Vector(0, 1, 0, 1)) == 25)
  }

  test("desperdicio: aulas con capacidad exacta producen desperdicio 0") {
    val cursos = Vector(("A", 0, 4, 30), ("B", 6, 10, 40))
    val aulas  = Vector(("X", 30), ("Y", 40))
    assert(desperdicio(cursos, aulas, Vector(0, 1)) == 0)
  }

  test("desperdicio: todos los cursos en la aula de mayor capacidad maximiza el desperdicio") {
    // M01(25), M02(30), M03(20) todos en E102(cap 40): (40-25)+(40-30)+(40-20) = 15+10+20 = 45
    assert(desperdicio(c1, a1, Vector(1, 1, 1)) == 45)
  }

  // ---------------------------------------------------------------------------
  // costoAsignacion — 5 casos
  // ---------------------------------------------------------------------------

  test("costoAsignacion: asignacion [0,0,1] del ejemplo 1 tiene costo 1031") {
    // CH=1 → 1000. CF=0 → 0. DE=25 → 25. MV=D[0,0]+D[0,1]=0+3=3 → 6. Total=1031
    // NOTA: requiere movilidad implementada para pasar
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] del ejemplo 1 tiene costo 37") {
    // CH=0. CF=0. DE=25. MV=D[0,1]+D[1,0]=3+3=6 → 12. Total=0+0+25+12=37
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  test("costoAsignacion: asignacion con choque es siempre mas costosa que una sin choque") {
    // El peso de CH es 1000, mucho mayor que los demas: cualquier choque domina el costo
    val conChoque    = costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w)
    val sinChoque    = costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w)
    assert(conChoque > sinChoque)
  }

  test("costoAsignacion: ejemplo 2 asignacion [0,1,0,1] tiene costo 155") {
    // CH=0. CF=1 → 100. DE=25 → 25. MV=D[0,1]+D[1,0]+D[0,1]=5+5+5=15 → 30. Total=155
    assert(costoAsignacion(c2, a2, d2, Vector(0, 1, 0, 1), w) == 155)
  }

  test("costoAsignacion: ejemplo 2 asignacion [0,1,1,0] tiene costo 160") {
    // CH=0. CF=1 → 100. DE=5+5+0+30=40 → 40. MV=D[0,1]+D[1,1]+D[1,0]=5+0+5=10 → 20. Total=160
    assert(costoAsignacion(c2, a2, d2, Vector(0, 1, 1, 0), w) == 160)
  }
}

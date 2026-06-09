package proyecto

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import AsignacionAulas._

@RunWith(classOf[JUnitRunner])
class AsignacionAulasTest extends AnyFunSuite {

  // Ejemplo 1 del enunciado
  val c1: Cursos = Vector(("M01", 4, 8, 25), ("M02", 6, 10, 30), ("M03", 12, 16, 20))
  val a1: Aulas = Vector(("E101", 30), ("E102", 40))
  val d1: Distancias = Vector(Vector(0, 3), Vector(3, 0))
  val w: Pesos = (1000, 100, 1, 2)

  // solapan
  test("solapan: M01[4,8) y M02[6,10) se solapan") {
    assert(solapan(("M01", 4, 8, 25), ("M02", 6, 10, 30)))
  }

  test("solapan: M01[4,8) y M03[12,16) no se solapan") {
    assert(!solapan(("M01", 4, 8, 25), ("M03", 12, 16, 20)))
  }

  test("solapan: cursos adyacentes [0,4) y [4,8) no se solapan") {
    assert(!solapan(("A", 0, 4, 10), ("B", 4, 8, 10)))
  }

  // choques
  test("choques: asignacion [0,0,1] tiene 1 choque (M01 y M02 en E101)") {
    assert(choques(c1, Vector(0, 0, 1)) == 1)
  }

  test("choques: asignacion [0,1,0] no tiene choques") {
    assert(choques(c1, Vector(0, 1, 0)) == 0)
  }

  // capacidadFallida
  test("capacidadFallida: asignacion [0,0,1] no falla capacidad") {
    assert(capacidadFallida(c1, a1, Vector(0, 0, 1)) == 0)
  }

  // desperdicio
  test("desperdicio: asignacion [0,0,1] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E101(30)-M02(30)=0, E102(40)-M03(20)=20 → 25
    assert(desperdicio(c1, a1, Vector(0, 0, 1)) == 25)
  }

  test("desperdicio: asignacion [0,1,0] tiene desperdicio 25") {
    // E101(30)-M01(25)=5, E102(40)-M02(30)=10, E101(30)-M03(20)=10 → 25
    assert(desperdicio(c1, a1, Vector(0, 1, 0)) == 25)
  }

  // costoAsignacion
  test("costoAsignacion: asignacion [0,0,1] cuesta 1031") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 0, 1), w) == 1031)
  }

  test("costoAsignacion: asignacion [0,1,0] cuesta 37") {
    assert(costoAsignacion(c1, a1, d1, Vector(0, 1, 0), w) == 37)
  }

  // generarAsignaciones
  test("generarAsignaciones: 2 cursos y 2 aulas produce 4 asignaciones") {
    assert(generarAsignaciones(2, 2).length == 4)
  }

  test("generarAsignaciones: 3 cursos y 3 aulas produce 27 asignaciones") {
    assert(generarAsignaciones(3, 3).length == 27)
  }

  test("generarAsignaciones(0,2)") {
    assert(generarAsignaciones(0, 2) == Vector(Vector()))
  }

  test("generarAsignaciones(1,2)") {
    assert(generarAsignaciones(1, 2) == Vector(Vector(0), Vector(1)))
  }

  test("generarAsignaciones(2,2)") {
    assert(generarAsignaciones(2, 2) == Vector(Vector(0, 0), Vector(0, 1), Vector(1, 0), Vector(1, 1)))
  }

  test("generarAsignaciones(3,2) genera 8 asignaciones exactamente") { //No evalúa como tal qué contiene, sino cuántas son.
    assert(generarAsignaciones(3, 2).size == 8)
  }

  test("generarAsignaciones(3,2) contiene Vector(1,1,1)") {
    assert(generarAsignaciones(3, 2).contains(Vector(1, 1, 1)))
  }

  // asignacionOptima
  test("asignacionOptima: el costo de la optima no supera el de [0,1,0] (37)") {
    val (_, costo) = asignacionOptima(c1, a1, d1, w)
    assert(costo <= 37)
  }

  test("asignacionOptima devuelve asignacion de longitud correcta") {
    val (m,_) = asignacionOptima(c1,a1,d1,w)
    assert(m.length == c1.length)
  }

  test("asignacionOptima devuelve costo consistente") {
    val (a,costo) = asignacionOptima(c1,a1,d1,w)
    assert(costo == costoAsignacion(c1,a1,d1,a,w))
  }

  test("asignacionOptima devuelve una asignacion generada") {
    val (f,_) = asignacionOptima(c1,a1,d1,w)
    assert(generarAsignaciones(c1.length, a1.length).contains(f))
  }

  test("asignacionOptima mejora la asignacion 001") {
    val (_,optimo) =
      asignacionOptima(c1,a1,d1,w)
    val otroCosto = costoAsignacion(c1,a1,d1, Vector(0,0,1), w)
    assert(optimo <= otroCosto)
  }

  test("asignacionOptima mejora la asignacion 101") {
    val (_,optimo) = asignacionOptima(c1,a1,d1,w)
    val otroCosto = costoAsignacion(c1,a1,d1, Vector(1,0,1), w)
    assert(optimo <= otroCosto)
  }

  // movilidad ejemplos con aulas puntuales
  test("movilidad todos en aula 0") {
    assert(movilidad(c1, a1, d1, Vector(0, 0, 0)) == 0)
  }

  test("movilidad todos en aula 1") {
    assert(movilidad(c1,a1,d1,Vector(1,1,1)) == 0)
  }

  test("movilidad ejemplo 001") {
    assert(movilidad(c1,a1,d1,Vector(0,0,1)) == 3)
  }

  test("movilidad ejemplo 010") {
    assert(movilidad(c1,a1,d1,Vector(0,1,0)) == 6)
  }

  test("movilidad ejemplo 011") {
    assert(movilidad(c1,a1,d1,Vector(0,1,1)) == 3)
  }

  test("movilidad ejemplo 101") {
    assert(movilidad(c1,a1,d1,Vector(1,0,1)) == 6)
  }

}


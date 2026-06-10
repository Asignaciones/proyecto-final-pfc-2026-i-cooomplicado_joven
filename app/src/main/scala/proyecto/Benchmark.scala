package proyecto

import org.scalameter._
import AsignacionAulas._
import AsignacionAulasPar._

object Benchmark {

  // Fija una semilla para reproducibilidad
  val rng = new scala.util.Random(42)

  def medirTiempos(n: Int, m: Int): Unit = {
    // Generar entradas fijas para comparar seq vs par en las mismas condiciones
    val cursos = cursosAlAzar(n)
    val aulas  = aulasAlAzar(m)
    val d      = distanciasAlAzar(m)
    val w: Pesos = (1000, 100, 1, 2)

    // Warmup manual: se ejecuta una vez antes de medir para evitar JIT skew
    asignacionOptima(cursos, aulas, d, w)
    asignacionOptimaPar(cursos, aulas, d, w)

    val tSeq = measure { asignacionOptima(cursos, aulas, d, w) }
    val tPar = measure { asignacionOptimaPar(cursos, aulas, d, w) }

    val aceleracion = (tSeq.value - tPar.value) / tSeq.value * 100.0

    println(f"n=$n%2d  m=$m%2d  | Seq: ${tSeq.value}%8.3f ms  | Par: ${tPar.value}%8.3f ms  | Aceleración: $aceleracion%+.2f%%")
  }

  def main(args: Array[String]): Unit = {
    println("=" * 70)
    println("Benchmark: asignacionOptima vs asignacionOptimaPar")
    println("=" * 70)
    println(f"${"n"}%4s  ${"m"}%4s  | ${"Secuencial (ms)"}%16s  | ${"Paralela (ms)"}%14s  | Aceleración")
    println("-" * 70)

    // Progresión creciente de instancias
    // Espacio de búsqueda: 4, 8, 27, 81, 243, 729, 4096, 16384
    val instancias = List(
      (2, 2),   //      4 asignaciones — muy rápido, baseline
      (3, 2),   //      8 asignaciones
      (3, 3),   //     27 asignaciones
      (4, 3),   //     81 asignaciones
      (5, 3),   //    243 asignaciones
      (6, 3),   //    729 asignaciones
      (6, 4),   //   4096 asignaciones — empieza a notarse diferencia
      (7, 4)    //  16384 asignaciones — diferencia clara sin esperar mucho
    )

    instancias.foreach { case (n, m) => medirTiempos(n, m) }

    println("=" * 70)
    println()
    println("Nota: valores negativos en aceleración indican sobrecarga del")
    println("paralelismo (overhead > ganancia), típico en instancias pequeñas.")
  }
}
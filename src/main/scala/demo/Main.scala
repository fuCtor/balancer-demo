package demo

import com.twitter.conversions.DurationOps._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.loadbalancer.Balancers
import com.twitter.finagle.loadbalancer.aperture.ProcessCoordinate
import com.twitter.finagle.util.{DefaultTimer, Rng}
import com.twitter.util.{Await, Duration, Future}

import scala.util.Random

object Main extends com.twitter.app.App {
  import DefaultTimer.Implicit

  val serverSize   = 30
  val requestCount = 20

  ProcessCoordinate.setCoordinate(1, 2)

  val counter         = Counter(serverSize)
  val report          = Report(counter)
  val random          = new Random(42)
  val currentRng: Rng = Rng(random)

  def serviceBuilder(step: Duration)(idx: Int): Service[Request, Response] =
    Service.mk { _ =>
      Future.Unit.delayed(10.millis + step * idx).map(_ => Response(Status.NoContent))
    }

  val serverBuilder = ServerBuilder.build(counter, serviceBuilder(100.millis))

  def main() = {
    val servers = (0 until serverSize).map(serverBuilder)
    val runner  = TestRunner(report, servers, requestCount * serverSize)

    val balancers = Seq(
      "RoundRobin"      -> Balancers.roundRobin(),
      "HeapLeastLoaded" -> Balancers.heap(random),
      "AperturePeakEwma" -> Balancers
        .aperturePeakEwma(lowLoad = 1, highLoad = 2, rng = currentRng, useDeterministicOrdering = Some(true)),
      "AperturePeakEwma[random]" -> Balancers.aperturePeakEwma(lowLoad = 1, highLoad = 2, rng = currentRng),
      "P2CLeastLoaded"           -> Balancers.p2c(rng = currentRng),
      "P2CPeakEwma"              -> Balancers.p2cPeakEwma(decayTime = 10.second, rng = currentRng)
    )

    val f = for {
      _ <- Future.traverseSequentially(balancers)((runner.doTest(1) _).tupled)
      _ <- Future.traverseSequentially(balancers)((runner.doTest(2) _).tupled)
      _ <- Future.traverseSequentially(balancers)((runner.doTest(3) _).tupled)
      _ <- runner.stop
    } yield report.printSummary()

    Await.result(f)
  }

}

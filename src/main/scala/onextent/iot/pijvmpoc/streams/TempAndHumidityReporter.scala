package onextent.iot.pijvmpoc.streams

import akka.stream.ThrottleMode
import akka.stream.scaladsl.{Flow, Merge, Source}
import io.circe.generic.auto._
import io.circe.syntax._
import onextent.iot.pijvmpoc.Conf._
import onextent.iot.pijvmpoc.io.HttpReport
import onextent.iot.pijvmpoc.models.{TempReading, TempReport}

import scala.concurrent.duration._

/**
  * Akka Stream whose source is a temp and humidity module on a PI
  * and whose Sink reports to an https API
  */
object TempAndHumidityReporter {

  val client = HttpReport(authString, baseUrl)

  def apply(): Unit = {

    val throttlingFlow = Flow[(Int, TempReading)].throttle(
      elements = 1,
      //per = 1.minute,
      per = 5.seconds,
      maximumBurst = 0,
      mode = ThrottleMode.Shaping
    )
    val s1 = Source.fromGraph(new Dht22SensorSource(4)).via(throttlingFlow)
    val s2 = Source.fromGraph(new Dht22SensorSource(22)).via(throttlingFlow)

    Source
      .combine(s1, s2)(Merge(_))
      .runForeach(t => {
        println(s"from sensor ${t._1}")

        val report = TempReport(Some(s"navisensor-${t._1}"), t._2)
        println(report.asJson.spaces2)

        client(report.asJson.spaces2.getBytes("UTF8")) match {
          case Right(code) => println(s"http code: $code")
          case Left(error) => println(s"http error: $error")
        }

      })

  }

}

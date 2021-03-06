package onextent.iot.pijvmpoc.streams

import scala.concurrent.ExecutionContext.Implicits.global
import akka.{Done, NotUsed}
import akka.stream._
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttMessage, MqttQoS}
import akka.stream.scaladsl.{Flow, Merge, Sink, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.syntax._
import onextent.iot.pijvmpoc.Conf._
import onextent.iot.pijvmpoc.io.HttpReport
import onextent.iot.pijvmpoc.models.{TempReading, TempReport}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Akka Stream whose source is a temp and humidity module on a PI
  * and whose Sink reports to an https API
  */
object TempAndHumidityReporter extends LazyLogging {

  val client = HttpReport(authString, baseUrl)

  def apply(): Unit = {

    val throttlingFlow = Flow[(Int, Option[TempReading])].throttle(
      elements = 1,
      per = 30.seconds,
      maximumBurst = 0,
      mode = ThrottleMode.Shaping
    )

    val s1 = Source.fromGraph(new Dht22SensorSource(4)).via(throttlingFlow)

    val s2 = Source.fromGraph(new Dht22SensorSource(22)).via(throttlingFlow)

    val connectionSettings =
      MqttConnectionSettings(
        mqttUrl,
        "test-iot-client",
        new MemoryPersistence
      ).withAuth(mqttUser, mqttPwd)

    val sinkSettings = connectionSettings.withClientId(clientId = "sink-spec/sink")

    val mqttSink = MqttSink(sinkSettings, MqttQoS.atLeastOnce)

    def httpsSink: Sink[MqttMessage, Future[Done]] =
      Sink.foreach(t => {
        logger.debug(s"httpSink sending to ${t.topic}")
        client(t.payload.toArray) match {
          case Right(code) => logger.debug(s"http code: $code\n")
          case Left(error) => logger.warn(s"http error: $error\n")
        }
      })

    def tempReadings() =
      (t: (Int, Option[TempReading])) => {
        t._2 match {
          case Some(reading) =>
            List(TempReport(Some(s"navisensor-${t._1}"), reading))
          case _ =>
            List()
        }
      }

    def mqttReading() =
      (r: TempReport) => MqttMessage(mqttTopic, ByteString(r.asJson.noSpaces), Some(MqttQoS.AtLeastOnce), retained = true)

    val done: Future[Done] = Source
      .combine(s1, s2)(Merge(_))
      .mapConcat(tempReadings())
      .map(mqttReading())
      .alsoTo(httpsSink)
      .runWith(mqttSink)

    done onComplete {
      case Success(s) => logger.debug(s"stream got $s")
      case Failure(e) => logger.error(s"stream got $e")
    }

  }

}

package onextent.iot.pijvmpoc.streams

import akka.actor.ActorSystem
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import io.circe.generic.auto._
import onextent.iot.pijvmpoc.io.Dht22Sensor
import onextent.iot.pijvmpoc.models.TempReading

class Dht22SensorSource(pin: Int)(implicit system: ActorSystem)
    extends GraphStage[SourceShape[(Int,TempReading)]] {

  val out: Outlet[(Int, TempReading)] = Outlet("NdtReadingSource")

  override val shape: SourceShape[(Int, TempReading)] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = push(out, (pin, Dht22Sensor(pin)))
        }
      )
    }

}

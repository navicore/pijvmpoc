package onextent.iot.pijvmpoc.io

import io.circe
import io.circe.generic.auto._
import io.circe.parser._
import onextent.iot.pijvmpoc.models.TempReading

object Dht22Sensor {

  def apply(pin: Int): TempReading = {

    //val reading = Sensor.read(22, pin)
    //val json = JSON.stringify(reading)

    val json =
      """{"temperature": 25.1, "humidity": 99.9, "isValid": true, "errors": 0}"""

    val decoded: Either[circe.Error, TempReading] = decode[TempReading](json)

    // todo: keep the Either for error reporting via IOT but hide the circe type
    decoded match {
      case Left(e) =>
        println(s"no reading: $e")
        TempReading()
      case Right(r) => r
    }

  }

}

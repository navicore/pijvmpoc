package onextent.iot.pijvmpoc.io

import io.circe.generic.auto._
import onextent.iot.dht22.Dht22
import onextent.iot.pijvmpoc.models.TempReading

object Dht22Sensor {

  def apply(pin: Int): TempReading = {

    val dht22 = new Dht22

    val reading = dht22.get(pin)

    if (reading(0) > 0) {
      TempReading(Some(reading(2).toDouble), Some(reading(0).toDouble))
    } else {
      TempReading()
    }

  }

}

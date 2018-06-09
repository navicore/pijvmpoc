package onextent.iot.pijvmpoc

import onextent.iot.pijvmpoc.io.GpioBlinkExample
import onextent.iot.pijvmpoc.streams.TempAndHumidityReporter

object Main {

  def main(args: Array[String]): Unit = {

    println("starting...")

    GpioBlinkExample()

    TempAndHumidityReporter()

  }

}

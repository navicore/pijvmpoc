package onextent.iot.pijvmpoc

import onextent.iot.pijvmpoc.io._
import onextent.iot.pijvmpoc.streams.TempAndHumidityReporter

object Main {

  def main(args: Array[String]): Unit = {

    println("starting...")

    GpioBlinkExample()

    PwmLedExample()

    TempAndHumidityReporter()

  }

}

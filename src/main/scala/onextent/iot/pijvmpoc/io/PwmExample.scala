package onextent.iot.pijvmpoc.io

import com.pi4j.io.gpio._

object PwmExample {

  def apply(): Unit = {

    val gpio: GpioController = GpioFactory.getInstance()

    val pin: Pin = RaspiPin.GPIO_16

    val pwm = gpio.provisionPwmOutputPin(pin)

    // more...
  }
}

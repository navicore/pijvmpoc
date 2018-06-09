package onextent.iot.pijvmpoc.io

import com.pi4j.io.gpio._

object GpioBlinkExample {

  def apply(): Unit = {

    val gpio: GpioController = GpioFactory.getInstance()

    val pin: Pin = RaspiPin.GPIO_16

    val pwm = gpio.provisionDigitalOutputPin(pin)

    pwm.blink(200)

    Thread.sleep(1000 * 10)

    gpio.shutdown()

  }
}

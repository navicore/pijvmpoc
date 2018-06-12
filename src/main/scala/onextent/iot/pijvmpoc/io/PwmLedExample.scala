package onextent.iot.pijvmpoc.io

import com.pi4j.io.gpio._

object PwmLedExample {

  def apply(): Unit = {

    println("pwm demo starting")

    GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING))

    val gpio: GpioController = GpioFactory.getInstance()

    //val pin: Pin = RaspiPin.GPIO_18
    val pin: Pin = RaspiBcmPin.GPIO_18
    //val pin: Pin = RaspiPin.GPIO_12

    val pwm = gpio.provisionPwmOutputPin(pin)

    pwm.setPwm(0)

    for (i <- 1 to 100) {
      pwm.setPwmRange(i)
      Thread.sleep(10)
    }

    for (i <- 100 to 1) {
      pwm.setPwmRange(i)
      Thread.sleep(10)
    }

    gpio.shutdown()
    println("pwm demo complete")

  }
}

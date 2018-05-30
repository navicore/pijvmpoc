package onextent.iot.pijvmpoc.models

//{"humidity":46.5,"temperature":23,"isValid":true,"errors":0}
case class TempReading(humidity: Option[Double] = None,
                       temperature: Option[Double] = None,
                       isValid: Option[Boolean] = None,
                       errors: Option[Int] = None)

case class TempReport(DevAddr: Option[String] = Some("unknown"), decoded_payload: TempReading)

case class Error()


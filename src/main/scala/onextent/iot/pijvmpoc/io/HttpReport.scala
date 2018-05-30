package onextent.iot.pijvmpoc.io

import com.softwaremill.sttp._

object HttpReport {

  def apply(authString: String,
            baseUrl: String): Array[Byte] => Either[String, Int] = {

    body: Array[Byte] =>
      {

        val request: Request[String, Nothing] = sttp
          .contentType("application/json")
          .headers("Content-type" -> "application/json", "Authorization" -> s"$authString")
          .body(body)
          .post(uri"$baseUrl")

        implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
        val response: Id[Response[String]] = request.send()

        response.body match {
          case Right(_) => Right(response.code)
          case Left(e) => Left(s"${response.code} $e")
        }

      }

  }

}

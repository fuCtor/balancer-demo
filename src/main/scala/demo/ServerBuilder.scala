package demo

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service, SimpleFilter}
import com.twitter.util.Future

object ServerBuilder {
  def build(counter: Counter, serviceBuilder: Int => Service[Request, Response]): Int => ListeningServer = { idx: Int =>
    val filter = new SimpleFilter[Request, Response] {
      override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
        counter.count(idx)
        service(request)
      }
    }
    Http.server.serve("0.0.0.0:0", filter.andThen(serviceBuilder(idx)))
  }
}

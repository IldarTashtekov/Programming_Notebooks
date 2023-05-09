package controllers

import javax.inject._
import play.api.mvc._
import models.{Movie, MovieRepository}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MovieController @Inject()(cc: ControllerComponents,
                               movieRepository: MovieRepository
                              ) extends AbstractController(cc) {


  implicit val serializador = Json.format[Movie]
  def getMovies = Action.async{
    movieRepository
      .getAll
      .map(movies => {
        val j = Json.obj(
          fields = "data"->movies,
          "message" -> "Movies listed"
        )
        Ok(j)
      })
      .recover{
        case ex => InternalServerError(s"hubo un error: ${ex.getLocalizedMessage}")
      }
  }
  def getMovie(id:String) =  Action.async{
    movieRepository
      .getOne(id)
      .map(movies => {
        val j = Json.obj(
          fields = "data"->movies,
          "message" -> "Movies listed"
        )
        Ok(j)
      })
      .recover{
        case ex => InternalServerError(s"hubo un error: ${ex.getLocalizedMessage}")
      }
  }
  def createMovie = Action.async(parse.json){ request =>
      //validamos que el json sea del tipo que nos interesa
      //case Left(error) => envia un error ,case Right(movie) => si va bien
    val validador = request.body.validate[Movie]
    validador.asEither match{
      //si va mal devuelve Bad Request
      case Left(error) => Future.successful(BadRequest(error.toString()))
      //si va bien creamos todo
      case Right(movie) => {
        movieRepository
          .create(movie)
          .map(movies => {
            val j = Json.obj(
              fields = "data"->movies,
              "message" -> "Movies listed"
            )
            Ok(j)
          })
          .recover{
          case ex => InternalServerError(s"hubo un error: ${ex.getLocalizedMessage}")
        }
      }
    }
  }

  def updateMovie(id:String) = Action.async(parse.json) { request =>
    val movieValidator = request.body.validate[Movie]

    movieValidator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) => {
        movieRepository
          .update(id, movie)
          .map(movie => {
            val jsonMovie = Json.obj(
              "data" -> movie,
              "message" -> "Movie updated"
            )
            Ok(jsonMovie)
          })
          .recover {
            case exception =>
              println("Falló en updateMovie", exception)
              InternalServerError(
                s"Hubo un error: ${exception.getLocalizedMessage}")
          }
      }
    }
  }
  def deleteMovie(id:String) = Action.async {
    movieRepository
      .delete(id)
      .map(movie => {
        val jsonMovie = Json.obj(
          "data" -> movie,
          "message" -> s"Movie with id: $id deleted"
        )
        Ok(jsonMovie)
      })
      .recover {
        case exception =>
          println("Falló en deleteMovie", exception)
          InternalServerError(
            s"Hubo un error: ${exception.getLocalizedMessage}")
      }
  }

}
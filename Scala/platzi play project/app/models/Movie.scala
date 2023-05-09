package models

import java.util.UUID

case class Movie(
  id:Option[String] = Option(UUID.randomUUID.toString),
  title: String,
  year: Int,
  cover: String,
  description: String,
  duration: Int,
  contentRating: String,
  source: String,
  tags: Option[String]
  )

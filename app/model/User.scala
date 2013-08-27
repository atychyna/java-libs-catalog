package model


/**
 * @author Anton Tychyna
 */
case class User(login: String,
                password: String,
                name: Option[String] = None)
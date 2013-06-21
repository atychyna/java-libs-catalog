package model

import org.bson.types.ObjectId
import com.novus.salat.annotations._

/**
 * @author Anton Tychyna
 */
case class Project(id: ObjectId = new ObjectId, name: String, url: String, categoryId: ObjectId) {
  @Persist val nameLowerCase = name.toLowerCase
}

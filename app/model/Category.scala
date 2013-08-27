package model

import org.bson.types.ObjectId
import util.stringForUrl
import com.novus.salat.annotations.raw.Ignore


/**
 * @author Anton Tychyna
 */
case class Category(id: ObjectId = new ObjectId,
                    name: String,
                    children: Seq[Category] = Seq.empty,
                    @Ignore projectCount: Option[Int] = None) {
  val urlFriendlyName = stringForUrl(name)
}
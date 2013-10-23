package model

import org.joda.time.DateTime
import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
case class Comment(id: ObjectId = new ObjectId,
                   author: String,
                   text: String,
                   created: DateTime = DateTime.now)

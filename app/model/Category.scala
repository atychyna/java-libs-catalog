package model

import org.bson.types.ObjectId

/**
 * @author Anton Tychyna
 */
case class Category(id: ObjectId = new ObjectId, name: String, children: Seq[Category] = Seq.empty)
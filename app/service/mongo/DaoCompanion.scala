package service.mongo

import com.novus.salat.dao.{SalatDAO, ModelCompanion}

/**
 * @author Anton Tychyna
 */
trait DaoCompanion[ObjectType <: AnyRef, ID <: Any] {

  object companion extends ModelCompanion[ObjectType, ID] {
    val dao = salatDao
  }

  def salatDao: SalatDAO[ObjectType, ID]
}

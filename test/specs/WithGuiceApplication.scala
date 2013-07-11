package specs

import com.google.inject.Module
import play.api.test.{FakeApplication, WithApplication}

/**
 * @author Anton Tychyna
 */
class WithGuiceApplication[C <: Module](module: Class[C], app: FakeApplication = FakeApplication()) extends WithApplication(app.copy(additionalConfiguration = app.additionalConfiguration ++ Map("guice.module" -> module.getName)))
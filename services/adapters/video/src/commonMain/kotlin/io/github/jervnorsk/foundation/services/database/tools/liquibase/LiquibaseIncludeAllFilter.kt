package io.github.jervnorsk.foundation.services.database.tools.liquibase

import liquibase.changelog.IncludeAllFilter
import java.util.Comparator

class LiquibaseIncludeAllFilter : IncludeAllFilter {

    override fun include(changeLogPath: String?): Boolean =
        changeLogPath != null
            && !changeLogPath.matches(".*/foundation/.*".toRegex())
            && changeLogPath.matches(".*/changelog(|\\S+)[.]yaml".toRegex())
}

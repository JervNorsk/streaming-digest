package io.github.jervnorsk.foundation.services.database.orm.hibernate.naming.adapters

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

fun Identifier.toUpperCase(): Identifier =
    Identifier.toIdentifier(text.uppercase());

fun Identifier.removeEntitySuffix(): Identifier =
    Identifier.toIdentifier(text.replace("_entity".toRegex(), ""));

class LiquibaseNamingStrategy : CamelCaseToUnderscoresNamingStrategy() {

    override fun toPhysicalCatalogName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? =
        super.toPhysicalCatalogName(logicalName, jdbcEnvironment)?.toUpperCase()

    override fun toPhysicalSchemaName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? =
        super.toPhysicalSchemaName(logicalName, jdbcEnvironment)?.toUpperCase()

    override fun toPhysicalTableName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? =
        super.toPhysicalTableName(logicalName, jdbcEnvironment)?.removeEntitySuffix()?.toUpperCase()

    override fun toPhysicalSequenceName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? =
        super.toPhysicalSequenceName(logicalName, jdbcEnvironment)?.toUpperCase()

    override fun toPhysicalColumnName(logicalName: Identifier?, jdbcEnvironment: JdbcEnvironment?): Identifier? =
        super.toPhysicalColumnName(logicalName, jdbcEnvironment)?.removeEntitySuffix()?.toUpperCase()
}

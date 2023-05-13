import nu.studer.gradle.jooq.JooqEdition.OSS
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.LambdaConverter
import org.jooq.meta.jaxb.Logging.INFO
import nu.studer.gradle.jooq.*
import com.gitlab.davinkevin.podcastserver.database.*

plugins {
    id("java")
    id("org.flywaydb.flyway") version "9.8.3"
    id("nu.studer.jooq") version "8.0"
    id("build-plugin-database")
}

group = "com.gitlab.davinkevin.podcastserver.database"
version = "2023.5.0"

repositories {
    mavenCentral()
}

val db = project.extensions.getByType<DatabaseConfiguration>()

dependencies {
    jooqGenerator("org.postgresql:postgresql:42.5.1")
    compileOnly("org.postgresql:postgresql:42.5.1")
}

jooq {
    version.set("3.17.10")
    edition.set(OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = INFO
                jdbc.apply {
					driver = "org.postgresql.Driver"
					url = db.jdbc()
					user = db.user
					password = db.password
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        forcedTypes = listOf(
                            ForcedType()
                                .withUserType("java.nio.file.Path")
                                .withLambdaConverter(
                                    LambdaConverter()
                                        .withFrom("Path::of")
                                        .withTo("Path::toString")
                                )
                                .withIncludeExpression("""ITEM\.FILE_NAME""")
                        )
                        isIncludeTables = true
                        isIncludePackages = false
                        isIncludeUDTs = true
                        isIncludeSequences = true
                        isIncludePrimaryKeys = true
                        isIncludeUniqueKeys = true
                        isIncludeForeignKeys = true
                    }

                    target.apply {
                        packageName = "com.github.davinkevin.podcastserver.database"
                        directory = "${project.projectDir}/src/main/java"
                    }
                }
            }
        }
    }
}

flyway {
	url = db.jdbc()
	user = db.user
	password = db.password
	locations = arrayOf("filesystem:$projectDir/src/main/migrations/")

}

tasks.named<JooqGenerate>("generateJooq") {
	inputs.dir(file("src/main/migrations"))
		.withPropertyName("migrations")
		.withPathSensitivity(PathSensitivity.RELATIVE)

	allInputsDeclared.set(false)
    outputs.cacheIf { false }

	dependsOn("flywayMigrate")
}

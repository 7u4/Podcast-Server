val env: Map<String, String> = System.getenv()!!

if (env.containsKey("CI")) {
    val imageTags by extra(generateTagsList())
}

if (env.containsKey("POSTGRES_DB") && env.containsKey("POSTGRES_USER") && env.containsKey("POSTGRES_PASSWORD")) {
    val databaseParameters by extra(mapOf(
            "url" to "postgresql://postgres:5432/${env["POSTGRES_DB"]}",
            "user" to env["POSTGRES_USER"]!!,
            "password" to env["POSTGRES_PASSWORD"]!!,
            "sqlFiles" to "$buildDir/flyway/migrations/"
    ))
}

@Suppress("LocalVariableName")
fun generateTagsList(): Set<String> {
    val CI_COMMIT_TAG = env["CI_COMMIT_TAG"]
    val CI_COMMIT_REF_SLUG = env["CI_COMMIT_REF_SLUG"] ?: error("CI_COMMIT_REF_SLUG not defined")

    if (CI_COMMIT_TAG != null) {
        return setOf(CI_COMMIT_TAG)
    }

    if(CI_COMMIT_REF_SLUG != "master") {
        return setOf(CI_COMMIT_REF_SLUG)
    }

    return setOf(CI_COMMIT_REF_SLUG, "latest")
}

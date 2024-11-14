plugins {
    kotlin("jvm") version "2.0.0"
}


allprojects {
    repositories {
        mavenCentral()
    }
}
val http4kVersion: String by project
val junitVersion: String by project

apply(plugin = "kotlin")

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(platform("org.http4k:http4k-bom:$http4kVersion"))

        implementation("org.http4k:http4k-core")
        implementation("org.http4k:http4k-client-okhttp")
        implementation("org.http4k:http4k-cloudnative")
        implementation("org.http4k:http4k-contract")
        implementation("org.http4k:http4k-format-jackson")
        implementation("org.http4k:http4k-server-undertow")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

        testImplementation(platform("org.junit:junit-bom:$junitVersion"))

        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.http4k:http4k-testing-hamkrest")
        testImplementation("org.http4k:http4k-testing-chaos")
        testImplementation("org.http4k:http4k-testing-approval")
        testImplementation("org.http4k:http4k-testing-webdriver")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

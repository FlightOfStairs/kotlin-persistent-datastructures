plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.61"
    id("io.gitlab.arturbosch.detekt") version "1.3.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.13"
    id("com.github.ben-manes.versions") version "0.27.0"
}

repositories {
    jcenter()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.3.0")
}

detekt {
    toolVersion = "1.3.0"
    input = files("src/main/kotlin", "src/test/kotlin")

    config = files(projectDir.resolve("detekt-config.yml"))
    buildUponDefaultConfig = true

    baseline = projectDir.resolve("detekt-baseline.xml")

    failFast = false
}

tasks.withType<Wrapper> {
    gradleVersion = "6.0.1"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

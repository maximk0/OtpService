plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

application {
    mainClass.set("otpservice.main.Application")
}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    val logbackVersion = "1.5.13"
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")

    // Email
    implementation("com.sun.mail:javax.mail:1.6.2")

    // SMPP
    implementation("org.opensmpp:opensmpp-core:3.0.2")

    // HTTP client
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    // JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    // PostgreSQL
    implementation("org.postgresql:postgresql:42.7.5")

    // JWT
    implementation("com.auth0:java-jwt:3.18.1")

    // Servlet API (provided scope)
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "otpservice.main.Application",
            "Class-Path" to configurations.runtimeClasspath.get().joinToString(" ") { "lib/${it.name}" }
        )
    }
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into("${layout.buildDirectory.get()}/lib")
}

tasks.named("build") {
    dependsOn("copyDependencies")
}
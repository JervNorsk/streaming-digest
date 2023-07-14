import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Kotlin
    // ------------------------------------------------------------------------
    kotlin("multiplatform")
    kotlin("kapt")

    // Kotlin Plugin
    // ------------------------------------------------------------------------
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")

    // Spring
    // ------------------------------------------------------------------------
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    // Analytics
    // ------------------------------------------------------------------------
//    id("com.appland.appmap")
}

kotlin {
    jvm {
        withJava()
        tasks.withType<KotlinCompile> {}
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin
                // ------------------------------------------------------------
                implementation(kotlin("reflect"))

                // Blueprint
                // ------------------------------------------------------------
//                implementation("org.projectlombok:lombok:latest.release")
//                implementation("org.mapstruct:mapstruct:${extra["mapstruct.version"]}")

//                with(project) {
//                    dependencies {
//                        "kapt"("org.springframework.boot:spring-boot-configuration-processor")
//
////                        "kapt"("org.projectlombok:lombok:latest.release")
////                        "kapt"("org.projectlombok:lombok-mapstruct-binding:latest.release")
//                        "kapt"("org.mapstruct:mapstruct-processor:latest.release")
//                    }
//                }
            }
        }
        val jvmMain by getting {
            dependencies {
                // API
                // ------------------------------------------------------------
                implementation("org.springframework.boot:spring-boot-starter-actuator")
                implementation("org.springframework.boot:spring-boot-starter-webflux")

                // API Documentation
                // ------------------------------------------------------------
                implementation("org.springdoc:springdoc-openapi-starter-common:${extra["springdoc.openapi.version"]}")
                implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${extra["springdoc.openapi.version"]}")

                // Security
                // ------------------------------------------------------------
                implementation("org.springframework.boot:spring-boot-starter-security")
                implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

                // Datasource
                // ------------------------------------------------------------
                implementation("org.springframework.boot:spring-boot-starter-data-jpa")
                implementation("org.springframework.boot:spring-boot-starter-validation")

                implementation("org.hibernate.common:hibernate-commons-annotations:latest.release")
                implementation("net.bytebuddy:byte-buddy:latest.release")

                implementation("org.liquibase:liquibase-core")

                implementation("com.h2database:h2")
//                implementation("mysql:mysql-connector-java:${extra["mysql.version"]}")

                // devtools
                // ------------------------------------------------------------
                implementation("org.springframework.boot:spring-boot-devtools")
            }
        }
        val jvmTest by getting {
            dependencies {
                // Core
                // ------------------------------------------------------------
                implementation("org.springframework.boot:spring-boot-starter-test")

                // API
                // ------------------------------------------------------------
//                implementation("io.projectreactor:reactor-test")

                // Security
                // ------------------------------------------------------------
//                implementation("org.springframework.security:spring-security-test")
//                implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
            }
        }
    }
}

//appmap {
//    debug = "info"
//    eventValueSize = 1024
//}

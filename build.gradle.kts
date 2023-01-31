import com.soywiz.korge.gradle.*

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    alias(libs.plugins.korge)
}

repositories {
    // Required to download KtLint
    mavenCentral()
}

korge {
    id = "com.sample.demo"

// To enable all targets at once

    targetAll()

// To enable targets based on properties/environment variables
    // targetDefault()

// To selectively enable targets
	
    // targetJvm()
    // targetJs()
    // targetDesktop()
    // targetIos()
    // targetAndroidIndirect() // targetAndroidDirect()

    // serializationJson()
    // targetAndroidDirect()
}
ktlint {
    disabledRules.value(listOf("no-wildcard-imports", "no-unused-imports"))
    ignoreFailures.set(false)
    android.set(true)
    // "no-wildcard-imports"

    filter {
        exclude("bootstrap.kt")
        // include("/src/**")
    }
}
tasks.getByPath("runJvm").dependsOn("ktlintFormat")

dependencies {
    add("commonMainApi", project(":deps"))
    // add("commonMainApi", project(":korge-dragonbones"))
}

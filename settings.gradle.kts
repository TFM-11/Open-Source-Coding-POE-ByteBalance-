pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // ✅ Required for MPAndroidChart
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io") // ✅ Required for MPAndroidChart
    }
}

rootProject.name = "ByteBalanceApp"
include(":app")
 
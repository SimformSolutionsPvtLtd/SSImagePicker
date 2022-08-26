// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(ClassPaths.ANDROID_GRADLE)
        classpath(ClassPaths.KOTLIN_GRADLE)
        classpath(ClassPaths.BINTRAY)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jitPack()
        gradlePluginPortal()
    }
}
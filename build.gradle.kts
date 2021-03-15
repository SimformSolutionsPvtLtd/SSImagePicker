// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(ClassPaths.ANDROID_GRADLE)
        classpath(ClassPaths.KOTLIN_GRADLE)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        jitPack()
    }
}
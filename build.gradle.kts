// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(ClassPaths.ANDROID_GRADLE)
        classpath(ClassPaths.KOTLIN_GRADLE)
        classpath(ClassPaths.GITHUB_DCENDENTS)
        classpath(ClassPaths.BINTRAY)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        jitPack()
    }
}
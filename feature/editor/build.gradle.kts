plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinPluginCompose)
}

android {
    namespace = "com.bluewhaleyt.codewhaleide.feature.editor"
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    api(libs.soraEditor)
    coreLibraryDesugaring(libs.androidDesugarJdkLibs)
    implementation(project(":common"))
}
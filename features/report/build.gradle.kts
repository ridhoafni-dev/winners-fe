import dependencies.addDataModule

plugins {
    plugins.`android-feature-library`
}

android {
    namespace = "com.dailyapps.feature.report"
}

dependencies {
    addDataModule()
}
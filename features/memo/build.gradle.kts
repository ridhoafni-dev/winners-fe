import dependencies.addDataModule

plugins {
    plugins.`android-feature-library`
}

android {
    namespace = "com.dailyapps.feature.memo"
}

dependencies {
    addDataModule()
}
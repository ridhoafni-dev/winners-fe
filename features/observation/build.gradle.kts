import dependencies.addDataModule

plugins {
    plugins.`android-feature-library`
}

android {
    namespace = "com.dailyapps.observation"
}

dependencies {
    addDataModule()
}
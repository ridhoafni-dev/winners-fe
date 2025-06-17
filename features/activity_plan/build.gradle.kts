import dependencies.addDataModule

plugins {
    plugins.`android-feature-library`
}

android {
    namespace = "com.dailyapps.activity_plan"
}

dependencies {
    addDataModule()
}
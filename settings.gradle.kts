pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Winners"
include (":app")
include(":common")
include(":features:auth")
include(":data")
include(":di")
include(":domain")
include(":model:entity")
include(":model:apiresponse")
include(":features:home")
include(":features:absent")
include(":features:note")
include(":features:exam")
include(":features:score")
include(":features:observation")
include(":features:activity_plan")
include(":features:report")
include(":features:memo")

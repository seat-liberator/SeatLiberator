tasks.register("unitTest") {
    group = "verification"
    description = "Runs unit tests in this module tree"

    dependsOn(
        subprojects.mapNotNull { child ->
            child.tasks.findByName("unitTest")
        }
    )
}
object Versions {
    val compileSdkVersion = 27
    val minSdkVersion = 14
    val targetSdkVersion = 27
    val kotlin = "1.2.50"
}

object Deps {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    val kotlinpoet = "com.squareup:kotlinpoet:0.6.0"
}
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    jacoco
}

android {
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    namespace = "com.example.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "visionary.gobarber"
        minSdk = 24
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.3"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE-notice.md")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/ASL2.0")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

tasks.register<JacocoReport>("jacocoTestReportAndroid") {
    dependsOn("connectedDebugAndroidTest")

    group = "Reporting"
    description = "Generate Jacoco coverage reports for androidTest"

    val coverageSourceDirs = listOf(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    )

    val classFiles = fileTree("${buildDir}") {
        include(
            "intermediates/javac/debug/**/*.class",
            "tmp/kotlin-classes/debug/**/*.class"
        )
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "**/*Binding*.*",
            "**/com/example/app/controller/AgendamentoAdapter\$AgendamentoViewHolder.class"
        )
    }

    val executionDataFiles = fileTree(buildDir) {
        include(
            "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
            "outputs/code-coverage/connected/**/*.ec"
        )
    }

    sourceDirectories.from(files(coverageSourceDirs))
    classDirectories.from(files(classFiles))
    executionData.from(files(executionDataFiles))

    reports {
        html.required.set(true)
        xml.required.set(true)
        html.outputLocation.set(file("${buildDir}/reports/jacoco/androidTest/html"))
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.espresso.intents)
    implementation("org.mindrot:jbcrypt:0.4")

    androidTestImplementation("org.mockito:mockito-android:5.11.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.4.0")

    androidTestImplementation ("androidx.test.espresso.idling:idling-concurrent:3.4.0")

    androidTestImplementation ("androidx.test.uiautomator:uiautomator:2.2.0")

    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

jacoco {
    toolVersion = "0.8.8"
}
/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlinMultiplatform") {
            id = "app.tivi.kotlin.multiplatform"
            implementationClass = "app.tivi.gradle.KotlinMultiplatformConventionPlugin"
        }

        register("kotlinAndroid") {
            id = "app.tivi.kotlin.android"
            implementationClass = "app.tivi.gradle.KotlinAndroidConventionPlugin"
        }

        register("androidApplication") {
            id = "app.tivi.android.application"
            implementationClass = "app.tivi.gradle.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "app.tivi.android.library"
            implementationClass = "app.tivi.gradle.AndroidLibraryConventionPlugin"
        }

        register("androidTest") {
            id = "app.tivi.android.test"
            implementationClass = "app.tivi.gradle.AndroidTestConventionPlugin"
        }
    }
}

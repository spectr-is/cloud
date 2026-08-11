import net.kyori.indra.IndraExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.incendo.cloudbuildlogic.city
import org.incendo.cloudbuildlogic.javadoclinks.JavadocLinksExtension
import org.incendo.cloudbuildlogic.jmp

class CloudPublishingConventions : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.incendo.cloud-build-logic.publishing")

        target.extensions.configure(JavadocLinksExtension::class) {
            exclude(target.libs.immutablesValueAnnotations)
            exclude(target.libs.immutablesAnnotate)
        }

        if (!target.name.endsWith("-bom")) {
            target.dependencies {
                JavaPlugin.API_CONFIGURATION_NAME(platform(project(":cloud-bom")))
            }
        }

        target.extensions.configure(IndraExtension::class) {
            github("Incendo", "cloud") {
                ci(true)
            }
            mitLicense()

            configurePublications {
                pom {
                    developers {
                        city()
                        jmp()
                    }
                }
            }
        }

        target.extensions.configure(PublishingExtension::class) {
            val user = (target.findProperty("spectrisUsername") ?: System.getenv("spectrisUsername")) as? String
            val pass = (target.findProperty("spectrisPassword") ?: System.getenv("spectrisPassword")) as? String

            repositories {
                maven {
                    name = "spectris-snapshots"
                    url = target.uri("https://repo.spectr.is/snapshots/")
                    credentials {
                        username = user
                        password = pass
                    }
                }
                maven {
                    name = "spectris-releases"
                    url = target.uri("https://repo.spectr.is/releases/")
                    credentials {
                        username = user
                        password = pass
                    }
                }
            }
        }
    }
}

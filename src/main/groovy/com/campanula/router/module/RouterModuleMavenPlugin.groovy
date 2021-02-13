package com.campanula.router.module

import com.campanula.router.module.config.Maven
import com.campanula.router.module.config.MavenAuth
import com.campanula.router.module.config.ProjectSetting
import com.campanula.router.module.listener.MavenListener
import com.campanula.router.module.log.Log
import com.campanula.router.module.setting.RouterModuleSetting
import com.campanula.router.module.util.Text
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.plugins.signing.SigningPlugin

/**
 * this desc
 * @author maweidong* @date 2021-01-13
 * @see
 *
 * */
class RouterModuleMavenPlugin implements Plugin<Project> {

    def uploadArchivesListener = null
    RouterModuleSetting settings

    static boolean isReleaseBuild(String version) {
        return !version.toUpperCase().contains("SNAPSHOT")
    }

    static String getUrl(Project project, String url) {
        if (url.toLowerCase().startsWith("http")) return url
        return project.uri(url).toString()
    }

    @Override
    void apply(Project project) {
        def androidLibrary = project.plugins.findPlugin("com.android.library")
        def javaLibrary = project.plugins.findPlugin("java")
        if (!androidLibrary && !javaLibrary) {
            throw new IllegalStateException("plugin only support Android Library or Java Library")
        }
        project.apply plugin: MavenPlugin
        project.apply plugin: SigningPlugin

        if (uploadArchivesListener == null) {
            uploadArchivesListener = new MavenListener()
        } else {
            project.gradle.removeListener(uploadArchivesListener)
        }
        project.gradle.addListener(uploadArchivesListener)

        settings = RouterModuleSetting.getInstance(project)
        ProjectSetting projectSetting = settings.getProjectSetting(project.name)
        if (projectSetting == null) {
            throw new IllegalArgumentException("project name is $project.name, displayName is $project.displayName , get Project Setting is failed!! ")
        }

        if (projectSetting == null || projectSetting.maven == null || !projectSetting.maven.isValid()) {
            Log.d("maven config failed, projectSetting is null ${projectSetting == null} ")
            return
        }

        if (projectSetting == null || projectSetting.oss == null || projectSetting.maven == null) return
        MavenAuth release = projectSetting.oss.release
        MavenAuth snapshot = projectSetting.oss.snapshot
        Maven maven = projectSetting.maven

        project.afterEvaluate {
            project.uploadArchives {
                repositories {
                    mavenDeployer {
                        beforeDeployment { MavenDeployment deployment -> project.signing.signPom(deployment) }

                        pom.groupId = maven.group
                        pom.artifactId = maven.artifactId
                        pom.version = maven.version

                        if (!isReleaseBuild(maven.version) && snapshot != null) {
                            snapshotRepository(url: getUrl(project, snapshot.url)) {
                                if (!Text.isEmpty(snapshot.userName) && !Text.isEmpty(snapshot.password)) {
                                    authentication(userName: snapshot.userName, password: snapshot.password)
                                }
                            }
                        } else {
                            repository(url: getUrl(project, release.url)) {
                                if (!Text.isEmpty(release.userName) && !Text.isEmpty(release.password)) {
                                    authentication(userName: release.userName, password: release.password)
                                }
                            }
                        }

                        pom.project {
                            name maven.pomName
                            packaging maven.packaging
                            description maven.description
                            url maven.websiteUrl

                            scm {
                                url maven.vcsUrl
                                connection maven.developerEmail
                                developerConnection maven.developerEmail
                            }

                            developers {
                                developer {
                                    id maven.developerId
                                    name maven.developerName
                                }
                            }
                        }

                        pom.whenConfigured { pom ->
                            pom.dependencies.forEach { dep ->
                                if ("unspecified".equalsIgnoreCase(dep.getVersion())) {
                                    try {
                                        Log.i("dependencies => ${maven.group}:$maven.artifactId:$maven.version")
                                        dep.setGroupId(maven.group)
                                        dep.setVersion(maven.version)
                                    } catch (Exception e) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                }

            }

            project.extensions.configure("signing", { t ->
                t.required { isReleaseBuild(maven.version) && project.gradle.taskGraph.hasTask("uploadArchives") }
                t.sign project.configurations.archives
            })

            if (project.hasProperty("android")) {
                //将源码打包
                project.task(type: Jar, "sourcesJar") {
                    classifier = 'sources'
                    from project.android.sourceSets.main.java.srcDirs
                }

                //生成文档注释
                project.task(type: Javadoc, "javadoc") {
                    failOnError = false
                    source = project.android.sourceSets.main.java.srcDirs
                    ext.androidJar = "${project.android.sdkDirectory}/platforms/${project.android.compileSdkVersion}/android.jar"
                    options {
                        encoding 'utf-8'
                        charSet 'utf-8'
                        links 'http://docs.oracle.com/javase/7/docs/api/'
                        linksOffline "https://developer.android.com/reference", "${project.android.sdkDirectory}/docs/reference"
                    }
                    exclude '**/BuildConfig.java'
                    exclude '**/R.java'
                    options.encoding = 'utf-8'
                    classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
                }

            } else {
                // Java libraries
                project.task(type: Jar, dependsOn: project.getTasksByName("classes", true), "sourcesJar") {
                    classifier = 'sources'
                    from project.sourceSets.main.allSource
                }
            }

            //将文档打包成jar
            project.task([type: Jar, dependsOn: project.getTasksByName("javadoc", true)], "javadocJar") {
                classifier = 'javadoc'
                from project.javadoc.destinationDir
            }

            project.artifacts {
                archives project.sourcesJar
                archives project.javadocJar
            }

            project.task("javaDocBuild") {
                project.sourcesJar
                project.javadocJar
            }
        }
    }
}

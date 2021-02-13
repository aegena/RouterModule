package com.campanula.router.module

import com.campanula.router.module.config.ProjectSetting
import com.campanula.router.module.exception.PluginException
import com.campanula.router.module.log.Log
import com.campanula.router.module.setting.RouterModuleSetting
import com.campanula.router.module.util.Text
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownProjectException
import org.gradle.api.artifacts.DependencySubstitution
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository

/**
 * 寻址插件
 * @author maweidong* @date 2020-12-18
 */
class RouterModulePlugin implements Plugin<Project> {

    RouterModuleSetting settings
    Project project

    @Override
    void apply(Project project) {
        this.project = project
        def androidProject = project.plugins.findPlugin("com.android.application")
        def androidLibrary = project.plugins.findPlugin("com.android.library")
        def javaLibrary = project.plugins.findPlugin("java")
        if (!androidProject && !androidLibrary && !javaLibrary) {
            throw new IllegalStateException("plugin only support Android Project or Android Library or Java Library")
        }
        Log.d("------ this project name is $project.name ---- displayName is $project.displayName------")
        settings = RouterModuleSetting.getInstance(project)
        addMavenRepositories()
        cacheChangingModulesTime(0)
        addDSLDynamicMethod()
        addProjectMavenPlugin()
        replaceDependency()


    }

    // 添加maven仓库
    void addMavenRepositories() {
        if (!settings.uploadArchive) return

        ProjectSetting projectSetting = settings.getProjectSetting(project.name)

        if (projectSetting == null) {
            throw new IllegalArgumentException("project name is $project.name, displayName is $project.displayName , get Project Setting is failed!! ")
        }

        if (projectSetting == null || projectSetting.maven == null || !projectSetting.maven.isValid()) {
            Log.d("maven config failed, projectSetting is null ${projectSetting == null} ")
            return
        }
        if (projectSetting == null || projectSetting.oss == null) return

        if (projectSetting.oss.release != null && !Text.isEmpty(projectSetting.oss.release.url)) {
            addMavenRepositories(projectSetting.oss.release.url)
        }
        if (projectSetting.oss.snapshot != null && !Text.isEmpty(projectSetting.oss.snapshot.url)) {
            addMavenRepositories(projectSetting.oss.snapshot.url)
        }
    }

    void addMavenRepositories(String mavenUrl) {
        Log.i("addMavenRepositories $mavenUrl")
        int size = project.repositories.size()
        for (int index = 0; index < size; index++) {
            ArtifactRepository repository = project.repositories.get(index)
            if (repository instanceof MavenArtifactRepository) {
                DefaultMavenArtifactRepository mvnRepository = repository as DefaultMavenArtifactRepository
                if (mvnRepository.url.toString().equalsIgnoreCase(mavenUrl)) {
                    return
                }
            }
        }

        MavenArtifactRepository repository = project.repositories.maven {
            if (mavenUrl.toLowerCase().startsWith("http")) {
                url mavenUrl
            } else {
                url project.uri(mavenUrl)
            }
        }
        project.repositories.remove(repository)
        project.repositories.add(0, repository)
        Log.i("addMavenRepositories, $repository.url")
    }

    void addDSLDynamicMethod() {
        project.rootProject.ext {
            dynamic = { path ->
                def parentName = project.getParent().name
                Log.d("dynamic, parent project name：$parentName , path: $path")
                if (settings != null && settings.isDebug(parentName)) {
                    try {
                        Log.i("dynamic ($path) --> project(: $parentName $path)")
                        return project.project(":$parentName$path")
                    } catch (UnknownProjectException exception) {
                        throw new PluginException("module \"${parentName}${path}\" not exist!!!", exception)
                    }
                } else {
                    try {
                        Log.i("dynamic ($path) --> project(: $parentName $path)")
                        return project.project("$path")
                    } catch (UnknownProjectException exception) {
                        throw new PluginException("module \"${parentName}${path}\" not exist!!!", exception)
                    }
                }
            }
        }
    }


    void replaceDependency() {
        project.afterEvaluate {
            project.configurations.all {
                resolutionStrategy.dependencySubstitution {
                    all { DependencySubstitution dependency ->
                        if (dependency.requested instanceof ModuleComponentSelector) {
                            ModuleComponentSelector selector = dependency.requested as ModuleComponentSelector
                            if (settings != null) {
                                try {
                                    String projectName = settings.getLocalProjectName(selector.group, selector.module)
                                    if (projectName != null && projectName.length() > 0) {
                                        Log.i("${selector} ==> project(:${projectName}:${selector.module})")
                                        dependency.useTarget project.project(":${projectName}:${selector.module}")
                                    }
                                } catch (Exception e) {
                                    throw new PluginException("replaceDependency failed!", e)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void addProjectMavenPlugin() {
        if (settings == null || !settings.uploadArchive) return
        project.afterEvaluate {
            if (project.plugins.hasPlugin('com.android.library') || project.plugins.hasPlugin('java')) {
                project.apply plugin: RouterModuleMavenPlugin
            }
        }

    }

    void cacheChangingModulesTime(int seconds) {
        project.configurations.all {
            resolutionStrategy.cacheChangingModulesFor seconds, 'seconds'
        }

    }
}

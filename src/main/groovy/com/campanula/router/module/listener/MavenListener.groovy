package com.campanula.router.module.listener

import com.campanula.router.module.config.Maven
import com.campanula.router.module.config.ProjectSetting
import com.campanula.router.module.log.Log
import com.campanula.router.module.setting.RouterModuleSetting
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class MavenListener implements TaskExecutionListener, BuildListener {

    List<Maven> archives = new ArrayList<>()
    RouterModuleSetting mSetting

    @Override
    @Deprecated
    void buildStarted(Gradle gradle) {

    }

    @Override
    void settingsEvaluated(Settings settings) {

    }

    @Override
    void projectsLoaded(Gradle gradle) {

    }

    @Override
    void projectsEvaluated(Gradle gradle) {

    }

    @Override
    void buildFinished(BuildResult buildResult) {
        def settings = buildResult.gradle.ext.pluginSetting
        if (settings == null) {
            Log.e("pluginSetting is null")
            return
        }
        def projectName = buildResult.gradle.rootProject.name

        if (buildResult.failure == null && archives.size() > 0) {
            Log.i("publish complete!")
            settings.send(projectName, archives)
            archives.clear()
        }
    }

    @Override
    void beforeExecute(Task task) {
        if ("uploadArchives".equalsIgnoreCase(task.name)) {
            Log.i("${task.project.name} Publish...")
        }

        if (mSetting == null) {
            mSetting = RouterModuleSetting.getInstance(task.project)
        }
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        if (task.project.plugins.hasPlugin('maven') && "uploadArchives".equalsIgnoreCase(task.name)) {
            if (taskState.executed) {
                if (taskState.failure == null && mSetting != null) {
                    Log.i("uploadArchives ${task.project.name} success!")
                    ProjectSetting projectSetting = mSetting.getProjectSetting(task.project.name)
                    if (projectSetting != null) {
                        archives.add(projectSetting.maven)
                    } else {
                        Log.e("uploadArchives ${task.project.name} ProjectSetting is null")
                        archives.add(null)
                    }
                } else {
                    Log.e("uploadArchives ${task.project.name} failed!")
                }
            }
        }
    }
}

package com.campanula.router.module.setting


import com.campanula.router.module.config.MavenRepositories
import com.campanula.router.module.config.Maven
import com.campanula.router.module.config.Message
import com.campanula.router.module.config.ProjectSetting
import com.campanula.router.module.log.Log
import com.campanula.router.module.util.GradleUtil
import com.campanula.router.module.util.Text
import org.gradle.api.initialization.Settings

/**
 * 插件配置
 *
 * @author maweidong* date 2020-12-18
 */
class RouterModuleSetting {

    private final static String GRADLE_LOCALE_NAME = "CRouter.gradle"

    boolean uploadArchive = false
    boolean notifyUser = false
    boolean pluginLogger = false
    Map<String, ProjectSetting> projects = new LinkedHashMap<>()
    Maven maven
    MavenRepositories oss
    Message message
    Map<String, Boolean> modules

    boolean isDebug(String projectName) {
        if (projects == null || projects.isEmpty() || Text.isEmpty(projectName)) return false
        ProjectSetting projectSetting = projects.get(projectName)
        if (projectSetting == null) return false
        return projectSetting.debug
    }


    static RouterModuleSetting getInstance(def setting) {
        if (setting.gradle.ext.has("pluginSetting")) {
            return setting.gradle.ext.pluginSetting
        }
        RouterModuleSetting pluginSetting = setting.extensions.findByName('CRouter')
        if (pluginSetting == null) {
            pluginSetting = setting.extensions.create('CRouter', RouterModuleSetting)
        }

        String gradleLocaleName = setting.rootDir.absolutePath + File.separator + GRADLE_LOCALE_NAME
        if (!GradleUtil.isValidGradleFile(gradleLocaleName)) {
            throw new IllegalArgumentException("root configuration error! please file is .gradle, \n $gradleLocaleName")
        }

        setting.apply from: gradleLocaleName
        Log.debug = pluginSetting.pluginLogger

        return pluginSetting
    }


    void saveCache(Settings settings) {
        settings.gradle.ext.pluginSetting = this
        Log.d("$this")
    }

    void printProjectSetting() {
        for (Map.Entry<String, ProjectSetting> entry : projects) {
            Log.d("$entry.key ==> $entry.value")
        }
    }

    ProjectSetting getProjectSetting(String projectName) {
        Log.d("get ProjectSetting Setting name is $projectName")
        if (projects == null || projects.isEmpty()) return null

        return projects.get(projectName)
    }

    String getLocalProjectName(String group, String module) {
        if (projects == null || projects.isEmpty()) return null
        for (Map.Entry<String, ProjectSetting> entry : projects.entrySet()) {
            if (!entry.value.debug) continue
            if (entry.value.maven.group == group && entry.value.maven.artifactId == module) {
                return entry.key
            }
        }
        return null
    }

    void send(def projectName, List<Maven> archives) {
        if (!notifyUser || archives == null || archives.isEmpty()) return
        Log.d("send projectName $projectName")
        for (Maven maven : archives) {
            if (maven == null) continue
            if (Text.isEmpty(maven.pomName)) continue
            ProjectSetting projectSetting = projects.get(maven.pomName)
            if (projectSetting == null || projectSetting.message == null) continue
            if (projectSetting.message.dingTalk != null) {
                projectSetting.message.dingTalk.send("sss")
            }
            if (projectSetting.message.weChat != null) {

            }
            if (projectSetting.message.webHook != null) {

            }
        }
    }
}

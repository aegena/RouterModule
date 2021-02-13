package com.campanula.router.module

import com.campanula.router.module.config.Maven
import com.campanula.router.module.config.MavenRepositories
import com.campanula.router.module.config.Message
import com.campanula.router.module.config.ProjectSetting
import com.campanula.router.module.log.Log
import com.campanula.router.module.setting.RouterModuleSetting
import com.campanula.router.module.util.GitShellUtil
import com.campanula.router.module.util.Text
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * ----- this desc -----
 * @author maweidong* @date 2021-01-19
 * @see
 *
 * */
class RouterModuleSettingPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        def mSetting = RouterModuleSetting.getInstance(settings)
        if (mSetting == null) return
        if (mSetting.projects == null) return
        Log.d(" init begin plugin config data ")

        if (mSetting.modules == null) {
            mSetting.modules = new HashMap<>(0)
        }

        for (Map.Entry<String, ProjectSetting> entry : mSetting.projects.entrySet()) {
            def projectName = entry.key
            def projectSetting = entry.value
            projectSetting.debug = mSetting.modules.get(projectName) == null ? Boolean.FALSE : mSetting.modules.get(projectName)
            Log.d("$projectName projectSetting.debug:${projectSetting.debug}")
            if (!projectSetting.debug) continue
            def directory = projectSetting.directory
            if (Text.isEmpty(directory)) {
                directory = "${settings.rootDir.getParentFile().path}$File.separator${projectName}"
                Log.d("$projectName directory is empty set default path is:$directory")
                projectSetting.directory = directory
            }

            def moduleDirectory = new File(directory)

            if (!moduleDirectory.exists()) {
                moduleDirectory.exists()
                Log.i("create $projectName directory success!!!\n")
                if (Text.isEmpty(projectSetting.git) || Text.isEmpty(projectSetting.branch)) {
                    Log.e("\"$projectName directory no exist ,git , branch is must need\"")
                    continue
                }
                if (GitShellUtil.clone(projectSetting.git, projectSetting.branch, directory) > 0) {
                    Log.e("$projectName clone failed!!!\n")
                    continue
                } else {
                    Log.e("$projectName clone success!!!\n")
                }
            }

            Log.i("include $projectName ; project(':${projectName}').projectDir = ${moduleDirectory}")

            projectSetting.maven = syncMavenConfig(projectSetting.maven as Maven, mSetting.maven as Maven)
            Log.d("$projectName maven sync after ${projectSetting.maven == null ? "" : projectSetting.maven.toString()}")

            projectSetting.oss = syncMavenRepositories(projectSetting.oss as MavenRepositories, mSetting.oss as MavenRepositories)
            Log.d("$projectName oss sync after ${projectSetting.oss == null ? "" : projectSetting.oss.toString()}")

            projectSetting.message = syncMessage(projectSetting.message as Message, mSetting.message as Message)
            Log.d("$projectName message sync after ${projectSetting.message == null ? "" : projectSetting.message.toString()}")

            settings.include "$projectName"
            settings.project(":$projectName").projectDir = moduleDirectory

        }
        Log.d(" init end plugin config data ")
        mSetting.saveCache(settings)
    }

    private static Maven syncMavenConfig(Maven pMaven, Maven maven) {
        if (maven == null) return pMaven
        if (pMaven == null) {
            pMaven = maven
            return pMaven
        }
        pMaven.name = Text.syncString(pMaven.name, maven.name)
        pMaven.group = Text.syncString(pMaven.group, maven.group)
        pMaven.artifactId = Text.syncString(pMaven.artifactId, maven.artifactId)
        pMaven.version = Text.syncString(pMaven.version, maven.version)
        pMaven.versionCode = Text.syncString(pMaven.versionCode, maven.versionCode)
        pMaven.pomName = Text.syncString(pMaven.pomName, maven.pomName)
        pMaven.description = Text.syncString(pMaven.description, maven.description)
        pMaven.packaging = Text.syncString(pMaven.packaging, maven.packaging)
        pMaven.websiteUrl = Text.syncString(pMaven.websiteUrl, maven.websiteUrl)
        pMaven.issueUrl = Text.syncString(pMaven.issueUrl, maven.issueUrl)
        pMaven.vcsUrl = Text.syncString(pMaven.vcsUrl, maven.vcsUrl)
        pMaven.developerId = Text.syncString(pMaven.developerId, maven.developerId)
        pMaven.developerName = Text.syncString(pMaven.developerName, maven.developerName)
        pMaven.developerEmail = Text.syncString(pMaven.developerEmail, maven.developerEmail)
        return pMaven
    }

    private static syncMavenRepositories(MavenRepositories pRepositories, MavenRepositories repositories) {
        if (repositories == null) return pRepositories
        if (repositories.snapshot == null && repositories.release == null) return pRepositories
        if (pRepositories == null) {
            pRepositories = repositories
            return pRepositories
        }
        if (pRepositories.snapshot == null) {
            pRepositories.snapshot = repositories.snapshot
        } else {
            pRepositories.snapshot.userName = Text.syncString(pRepositories.snapshot.userName, repositories.snapshot.userName)
            pRepositories.snapshot.password = Text.syncString(pRepositories.snapshot.password, repositories.snapshot.password)
            pRepositories.snapshot.url = Text.syncString(pRepositories.snapshot.url, repositories.snapshot.url)
        }
        if (pRepositories.release == null) {
            pRepositories.release = repositories.release
        } else {
            pRepositories.release.userName = Text.syncString(pRepositories.release.userName, repositories.release.userName)
            pRepositories.release.password = Text.syncString(pRepositories.release.password, repositories.release.password)
            pRepositories.release.url = Text.syncString(pRepositories.release.url, repositories.release.url)
        }
        return pRepositories
    }


    private static Message syncMessage(Message pMessage, Message message) {
        if (message == null) return pMessage
        if (message.dingTalk == null && message.webHook == null && message.weChat == null) return pMessage

        if (pMessage == null) {
            pMessage = message
            return pMessage
        }

        if (pMessage.dingTalk == null) {
            pMessage.dingTalk = message.dingTalk
        } else {

        }

        if (pMessage.weChat == null) {
            pMessage.weChat = message.weChat
        } else {

        }
        if (pMessage.webHook == null) {
            pMessage.webHook = message.webHook
        } else {

        }
        return pMessage


    }

}


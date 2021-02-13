package com.campanula.router.module.util

import com.campanula.router.module.exception.PluginException
import com.campanula.router.module.log.Log


/**
 * Git Tools
 *
 * @atuhor maweidong* date 2020-12-18
 */
class GitShellUtil {

    static int clone(String url, String branch, String dir) throws PluginException {
        def cmd = "git clone -b ${branch} ${url} ${dir}"
        return exec(cmd)
    }

    static int exec(def cmd) throws PluginException {
        Log.w("$cmd")
        def builder = new ProcessBuilder(cmd.split(" "))
        builder.redirectErrorStream(true)
        Process process = builder.start()
        InputStream stdout = process.getInputStream()
        def reader = new BufferedReader(new InputStreamReader(stdout))
        def line
        while ((line = reader.lines()) != null) {
            Log.w("$line")
        }
        process.waitFor()
        return process.exitValue()
    }

    static String getBranch() throws PluginException {
        return "git symbolic-ref --short -1 HEAD".execute().text.trim()
    }

    static String getDevName() throws PluginException {
        return "git config user.name".execute().text.trim()
    }

}

package com.campanula.router.module.util;


import com.campanula.router.module.exception.PluginException;
import com.campanula.router.module.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 命令执行器
 *
 * @author maweidong
 * date 2020-12-18
 */
public class ExecUtil {

    public static int exec(String... command) throws IOException, InterruptedException, PluginException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();
        InputStream stdout = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
        String line;
        while ((line = reader.readLine()) != null) {
            Log.w(line);
        }
        process.waitFor();
        int exitCode = process.exitValue();
        Log.d("exitCode:" + exitCode);
        return exitCode;
    }
}

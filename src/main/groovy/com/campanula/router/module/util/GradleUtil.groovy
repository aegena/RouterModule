package com.campanula.router.module.util


import com.campanula.router.module.log.Log


/**
 * this desc
 * @author maweidong* @date 2021-01-16
 * @see
 *
 * */
class GradleUtil {

    /**
     * 验证是否是Gradle文件
     * @param path
     * @return
     */
    static boolean isValidGradleFile(String path) {

        if (Text.isEmpty(path) || !path.endsWith(".gradle")) {
            Log.e("path is null or path suffix not .gradle, \npath : $path")
            return false
        }
        if (!new File(path).exists()) {
            Log.e("file not exists, \npath: $path")
            return false
        }
        return true

    }


}



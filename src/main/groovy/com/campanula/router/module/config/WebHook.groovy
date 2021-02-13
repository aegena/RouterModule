package com.campanula.router.module.config

import com.campanula.router.module.util.ExecUtil
import groovy.json.JsonOutput

/**
 * this desc
 * @author maweidong* @date 2021-02-12
 * @see
 * 
 * *    */
class WebHook implements Serializable {
    String url
    String userName
    /**
     * 发送自定义WebHook
     */
    int send(String text) {
        return ExecUtil.exec('curl',
                "-s",
                url,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(text))
    }
}

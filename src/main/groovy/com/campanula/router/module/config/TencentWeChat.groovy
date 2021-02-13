package com.campanula.router.module.config

import com.campanula.router.module.util.ExecUtil
import groovy.json.JsonOutput

/**
 * this desc
 * @author maweidong* @date 2021-02-12
 * @see
 * 
 * *    */
class TencentWeChat implements Serializable {
    String url
    String userName

    private static class MarkdownMessage {
        String msgtype
        Markdown markdown

        MarkdownMessage(String content) {
            this.msgtype = "markdown"
            this.markdown = new Markdown(content)
        }
    }

    private static class Markdown {
        String content

        Markdown(String content) {
            this.content = content
        }
    }
    /**
     * 发送通知到企业微信
     */
    int send(String text) {
        return ExecUtil.exec('curl',
                "-s",
                url,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(new MarkdownMessage(text)))
    }
}

package com.campanula.router.module.config

import com.campanula.router.module.util.ExecUtil
import groovy.json.JsonOutput

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * this desc
 * @author maweidong* @date 2021-02-12
 * @see
 * 
 * *                  */
class AlibabaDingTalk implements Serializable {
    String url
    String secret
    boolean atAll = false
    List<String> mobiles
    String userName

    int send(String text) {
        Long timestamp = System.currentTimeMillis()
        String stringToSign = timestamp + "\n" + secret
        Mac mac = Mac.getInstance("HmacSHA256")
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"))
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"))

        String sign = URLEncoder.encode(signData.encodeBase64().toString(), "UTF-8")

        return ExecUtil.exec('curl',
                "-s",
                url + '&timestamp=' + timestamp + '&sign=' + sign,
                "-H",
                "Content-Type: application/json",
                '-d',
                JsonOutput.toJson(new MarkdownMessage(text, atAll, mobiles)))
    }

    private static class MarkdownMessage {
        String msgtype
        Markdown markdown

        MarkdownMessage(String text, boolean atAll, List<String> mobiles) {
            this.markdown = new Markdown(text, atAll, mobiles)
            this.msgtype = "markdown"
        }
    }

    private static class Markdown {
        String title
        String text
        At at

        Markdown(String text) {
            this.text = text
        }

        Markdown(String text, boolean atAll, List<String> mobiles) {
            this.title = "Publish Success!"
            this.text = text
            this.at = new At(atAll, mobiles)
        }
    }

    private static class At {
        boolean atAll
        List<String> mobiles

        At() {
            this.atAll = false
        }

        At(boolean atAll, List<String> mobiles) {
            this.atAll = atAll
            this.mobiles = mobiles
        }

        At(List<String> mobiles) {
            this.mobiles = mobiles
        }
    }

}

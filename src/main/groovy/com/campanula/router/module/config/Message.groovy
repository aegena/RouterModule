package com.campanula.router.module.config

/**
 * this desc
 * @author maweidong* @date 2021-02-11
 * @see
 * 
 * *  */
class Message implements Serializable {
    AlibabaDingTalk dingTalk
    TencentWeChat weChat
    WebHook webHook


    @Override
    public String toString() {
        return "Message{" +
                "dingTalk=" + dingTalk +
                ", weChat=" + weChat +
                ", webHook=" + webHook +
                '}';
    }
}

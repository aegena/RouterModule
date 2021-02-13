package com.campanula.router.module.config


import com.campanula.router.module.util.Text

class Maven implements Serializable {
    //组件名
    String name
    //组件所属组
    String group
    //组件ID
    String artifactId
    //组件版本名称
    String version
    //组件版本号
    String versionCode
    //工程名称
    String pomName
    //组件说明
    String description
    //组件发布类型，aar或者jar
    String packaging
    //组件gitlab仓库地址
    String websiteUrl
    //issues 地址
    String issueUrl
    //Git仓库地址
    String vcsUrl
    //开发者ID
    String developerId
    //开发者名字
    String developerName
    //开发者邮箱
    String developerEmail


    Maven() {
    }


    boolean isValid() {
        return !Text.isEmpty(group) && !Text.isEmpty(artifactId)
    }




    @Override
    public String toString() {
        return "Maven{" +
                "name=" + name +
                ", group=" + group +
                ", artifactId=" + artifactId +
                ", version=" + version +
                ", versionCode=" + versionCode +
                ", pomName=" + pomName +
                ", description=" + description +
                ", packaging=" + packaging +
                ", websiteUrl=" + websiteUrl +
                ", issueUrl=" + issueUrl +
                ", vcsUrl=" + vcsUrl +
                ", developerId=" + developerId +
                ", developerName=" + developerName +
                ", developerEmail=" + developerEmail +
                '}';
    }
}

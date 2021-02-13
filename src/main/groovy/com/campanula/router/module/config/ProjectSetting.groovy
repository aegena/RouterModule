package com.campanula.router.module.config


class ProjectSetting implements Serializable {
    boolean debug
    String git
    String directory
    String branch
    Maven maven
    MavenRepositories oss
    Message message


    ProjectSetting() {
    }


    @Override
    public String toString() {
        return "ProjectSetting{" +
                "debug=" + debug +
                ", git='" + git + '\'' +
                ", directory='" + directory + '\'' +
                ", branch='" + branch + '\'' +
                ", MavenRepositories=" + oss +
                ", Message=" + message +
                '}';
    }
}

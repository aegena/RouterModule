package com.campanula.router.module.config

class MavenRepositories implements Serializable {

    MavenAuth release
    MavenAuth snapshot

    MavenRepositories() {

    }

    @Override
    public String toString() {
        return "MavenAuth{" +
                "release=" + release +
                ", snapshot=" + snapshot +
                '}';
    }
}

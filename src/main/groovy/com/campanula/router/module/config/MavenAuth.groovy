package com.campanula.router.module.config

/**
 * this desc
 * @author maweidong* @date 2021-02-11
 * @see
 * 
 * * */
class MavenAuth implements Serializable{
    String url
    String userName
    String password

    MavenAuth() {

    }



    @Override
    public String toString() {
        return "MavenAuth{" +
                "url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

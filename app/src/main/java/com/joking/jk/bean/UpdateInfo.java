package com.joking.jk.bean;

/**
 * Created by Administrator on 2016/10/9.
 * {"versioncode":1,"versionname":"1.0","description":"极客卫士为你保驾护航","downloadUri":"www.baidu.com"}
 */

public class UpdateInfo {
    public int versioncode;
    public String versionname;
    public String description;
    public String downloadUri;

    public String toString() {
        return "versionCode=" + versioncode + "versionName=" + versionname +
                "description=" + description + "downloadUri=" + downloadUri;
    }
}

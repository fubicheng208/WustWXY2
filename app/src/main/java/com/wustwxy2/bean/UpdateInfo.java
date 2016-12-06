package com.wustwxy2.bean;

/**
 * @author fubicheng
 * @ClassName:
 * @Description: TODO
 * @date 2016/9/26 19:58
 */
public class UpdateInfo {
    private String version;
    private String url;
    private String description;
    private String notification;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getNotification() {
        return notification;
    }
    public void setNotification(String notification) {
        this.notification = notification;
    }
}

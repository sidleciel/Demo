package com.rhodes.demo.Util;

import java.io.Serializable;

/**
 * Created by pengsk on 2015/2/6.
 */
public class IntentToDetialBean implements Serializable {
    private String appName;
    private float starNUmber;
    private String appId;
    private String IconUrl;
    /**
     * 跳转详情的时候使用。0 默认  1从对战跳转
     */
    private int detialType=0;


    public IntentToDetialBean() {

    }

    public IntentToDetialBean(String appName, float starNUmber, String appId, String iconUrl, int detialType) {
        this.appName = appName;
        this.starNUmber = starNUmber;
        this.appId = appId;
        IconUrl = iconUrl;
        this.detialType = detialType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public float getStarNUmber() {
        return starNUmber;
    }

    public void setStarNUmber(float starNUmber) {
        this.starNUmber = starNUmber;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public int getDetialType() {
        return detialType;
    }

    public void setDetialType(int detialType) {
        this.detialType = detialType;
    }
}

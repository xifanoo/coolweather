package com.sw.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xifanoo on 2017/6/9.
 */

public class Basic {
    @SerializedName("city")
    private String cityName;
    @SerializedName("id")
    private String weatherId;

    private Update update;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public static class Update{
        @SerializedName("loc")
        private String updateTime;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}

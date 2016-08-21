package com.example.admin.china_weather.Activity.util;

import android.text.TextUtils;

import com.example.admin.china_weather.Activity.db.ChinaWeatherDB;
import com.example.admin.china_weather.Activity.model.City;
import com.example.admin.china_weather.Activity.model.County;
import com.example.admin.china_weather.Activity.model.Province;

/**
 * Created by admin on 2016/8/21.
 */
public class Utility {
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvinceResponse(ChinaWeatherDB chinaWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length >0) {
                for (String p:allProvince) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    chinaWeatherDB.saveProvince(province);

                }
                return true;

            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的市级数据
     *
     */
    public static boolean handleCitiesResponse(ChinaWeatherDB chinaWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String c: allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                }
                return true;
            }
        }
        return false;
    }
    /*
    解析和处理服务器返回的县级数据

    * */
    public static boolean handleCountiesResponse(ChinaWeatherDB chinaWeatherDb,String response,int cityId){
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c : allCounties){
                   String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的书存储到County表
                    chinaWeatherDb.saveCounty(county);
                }
                return true;
            }

        }
        return false;
    }

}

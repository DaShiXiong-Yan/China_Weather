package com.example.admin.china_weather.Activity.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.china_weather.Activity.db.ChinaWeatherDB;
import com.example.admin.china_weather.Activity.model.City;
import com.example.admin.china_weather.Activity.model.County;
import com.example.admin.china_weather.Activity.model.Province;
import com.example.admin.china_weather.Activity.util.HttpCallbackListener;
import com.example.admin.china_weather.Activity.util.HttpUtil;
import com.example.admin.china_weather.Activity.util.Utility;
import com.example.admin.china_weather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/8/21.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView textView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ChinaWeatherDB chinaWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    /*省列表*/
    private List<Province> provinceList;
    /*市列表*/
    private List<City> cityList;
    /*县级市*/
    private List<County> countyList;
    /*选中的省份*/
    private Province selectdProvince;
    /*选中的城市*/
    private City selectedCity;
    /*选中的县级市*/
    private County selectedCounty;
    /*当前选中的级别*/
    private int currentLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        chinaWeatherDB = ChinaWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectdProvince = provinceList.get(position);
                    queryCities();

                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvince();//加载省级数据
    }
    /*查询全国所有的省，优先从数据库查，如果没有查到再去服务器上查询*/
    private void queryProvince(){
        provinceList = chinaWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;

        }else{
            queryFromServer(null,"province");
        }
    }
    /*查询所选省中的市，优先从数据库查询，如果没有再去服务器查询*/
    private void queryCities(){
        cityList = chinaWeatherDB.loadCities(selectdProvince.getId());
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectdProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectdProvince.getProvinceCode(),"city");
        }
    }
    /*查询所选中的县级市，若果没有，再去服务器查询*/
    private void queryCounties(){
        countyList = chinaWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }
    /*根据穿诶的代号和类型从服务器上查询省市县数据*/
    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http:/www.weather.com.cn/data/list3/city" + code + ".xml";

        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(chinaWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(chinaWeatherDB,response,selectdProvince.getId());

                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(chinaWeatherDB,response,selectedCity.getId());

                }
                if (result){
                    //通过runOnUIThread（）方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                //通过tunOnUIThread（）方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    /*显示进度条对话框*/
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /*关闭对话框*/
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
    /*捕获Back按键，根据当前的级别来判断，此时应该返回市级列表，省列表，还是直接退出*/

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvince();
        }else{
            finish();
        }
    }
}

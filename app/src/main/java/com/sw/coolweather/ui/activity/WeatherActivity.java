package com.sw.coolweather.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sw.coolweather.R;
import com.sw.coolweather.gson.Forecast;
import com.sw.coolweather.gson.Weather;
import com.sw.coolweather.service.AutoUpdateService;
import com.sw.coolweather.util.HttpUtil;
import com.sw.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvDegree;
    private TextView tvWeatherInfo;
    private LinearLayout llForecast;
    private TextView tvAQI;
    private TextView tvPM25;
    private TextView tvComfort;
    private TextView tvCarWash;
    private TextView tvSport;
    private ImageView ivBingPic;
    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;
    private Button btnNavigation;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public SwipeRefreshLayout getSwipeRefresh() {
        return swipeRefresh;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.getBasic().getWeatherId();
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气数据
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(ivBingPic);
        } else {
            loadBingPic();
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求城市天气
     *
     * @param weatherId 天气id
     */
    public void requestWeather(String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=3fe09b2f3a5247ccaabeefc7ee4c4550";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather 天气信息实例
     */
    private void showWeatherInfo(Weather weather) {
        if (weather != null && "ok".equals(weather.getStatus())) {
            String cityName = weather.getBasic().getCityName();
            String updateTime = weather.getBasic().getUpdate().getUpdateTime().split(" ")[1];
            String degree = weather.getNow().getTemperature() + "℃";
            String weatherInfo = weather.getNow().getMore().getInfo();
            tvTitle.setText(cityName);
            tvTime.setText(updateTime);
            tvDegree.setText(degree);
            tvWeatherInfo.setText(weatherInfo);
            llForecast.removeAllViews();
            for (Forecast forecast : weather.getForecastList()) {
                View view = LayoutInflater.from(this).inflate(R.layout.item_forecast, llForecast, false);
                TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
                TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
                TextView tvMax = (TextView) view.findViewById(R.id.tv_max);
                TextView tvMin = (TextView) view.findViewById(R.id.tv_min);
                tvDate.setText(forecast.getDate());
                tvInfo.setText(forecast.getMore().getInfo());
                tvMax.setText(forecast.getTamperature().getMax());
                tvMin.setText(forecast.getTamperature().getMin());
                llForecast.addView(view);
            }
            if (weather.getAqi() != null) {
                tvAQI.setText(weather.getAqi().getCity().getAqi());
                tvPM25.setText(weather.getAqi().getCity().getPm25());
            }

            String comfort = "舒适度：" + weather.getSuggestion().getComfort().getInfo();
            String carWash = "洗车指数：" + weather.getSuggestion().getCarWash().getInfo();
            String sport = "运动建议：" + weather.getSuggestion().getSport().getInfo();
            tvComfort.setText(comfort);
            tvCarWash.setText(carWash);
            tvSport.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else {
            Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpRequest(requestUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(ivBingPic);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initView() {
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        tvTitle = (TextView) findViewById(R.id.tv_title_city);
        tvTime = (TextView) findViewById(R.id.tv_title_time);
        tvDegree = (TextView) findViewById(R.id.tv_degree);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        llForecast = (LinearLayout) findViewById(R.id.ll_forecast);
        tvAQI = (TextView) findViewById(R.id.tv_aqi);
        tvPM25 = (TextView) findViewById(R.id.tv_pm25);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvSport = (TextView) findViewById(R.id.tv_sport);
        ivBingPic = (ImageView) findViewById(R.id.iv_bing_pic);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        btnNavigation = (Button) findViewById(R.id.btn_navigation);
    }
}

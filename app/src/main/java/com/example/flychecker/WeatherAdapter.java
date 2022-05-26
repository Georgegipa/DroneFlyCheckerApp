package com.example.flychecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class WeatherAdapter extends BaseAdapter {
    private Context mContext;
    private List<Weather> mWeatherList;

    // constructor
    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.mContext = context;
        this.mWeatherList = weatherList;
    }

    @Override
    public int getCount() {
        return mWeatherList.size();
    }

    @Override
    public Object getItem(int i) {
        return mWeatherList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_row2, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        // getting model data for the row
        Weather weather = mWeatherList.get(i);
        viewHolder.bindData(weather);

        return view;
    }

    //create a class that extends BaseAdapter
    private class ViewHolder {
        //add all textviews from list_item_row2.xml
        private TextView timeTv;
        private TextView windTv;
        private TextView percipTv;
        private TextView cloudcoverTv;
        private TextView tempTv;

        //constructor
        public ViewHolder(View view) {
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            windTv = (TextView) view.findViewById(R.id.tv_windinfo);
            percipTv = (TextView) view.findViewById(R.id.tv_persipitation);
            cloudcoverTv = (TextView) view.findViewById(R.id.tv_cloudcover);
            tempTv = (TextView) view.findViewById(R.id.tv_temperature);
        }

        //bind data to the textviews
        public void bindData(Weather weather) {
            timeTv.setText(weather.getTime());
            windTv.setText(String.valueOf(weather.getWindspeed_10m()));
            percipTv.setText(String.valueOf(weather.getPrecipitation()));
            cloudcoverTv.setText(String.valueOf(weather.getCloudcover()));
            tempTv.setText(String.valueOf(weather.getTemperature_2m()));
        }

    }

}

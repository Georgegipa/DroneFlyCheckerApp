package com.example.flychecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Weather> mWeatherList;

    public WeatherAdapter(Context context, List<Weather> weatherList) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mWeatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_row2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        holder.bind(mWeatherList.get(position));
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    //set the data to the view

    public void setmWeatherList(List<Weather> weatherList) {
        this.mWeatherList = weatherList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTv;
        private TextView windTv;
        private TextView percipTv;
        private TextView cloudcoverTv;
        private TextView tempTv;

        //constructor
        public ViewHolder(@NonNull View view) {
            super(view);
            timeTv = (TextView) view.findViewById(R.id.tv_time);
            windTv = (TextView) view.findViewById(R.id.tv_windinfo);
            percipTv = (TextView) view.findViewById(R.id.tv_persipitation);
            cloudcoverTv = (TextView) view.findViewById(R.id.tv_cloudcover);
            tempTv = (TextView) view.findViewById(R.id.tv_temperature);
        }
        public void bind(final Weather weather) {
            timeTv.setText(weather.getTime());
            windTv.setText(String.valueOf(weather.getWindspeed_10m()));
            percipTv.setText(String.valueOf(weather.getPrecipitation()));
            cloudcoverTv.setText(String.valueOf(weather.getCloudcover()));
            tempTv.setText(String.valueOf(weather.getTemperature_2m()));
        }
    }
}

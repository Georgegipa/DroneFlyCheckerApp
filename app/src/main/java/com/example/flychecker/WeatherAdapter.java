package com.example.flychecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {


    public interface OnItemClickListener {
        //void onItemClick(String item);
        void onItemClick(RawWeatherData item);
    }

    private Context mContext;
    private LayoutInflater inflater;
    private List<RawWeatherData> rawWeatherDataList;
    private View view;
    private final OnItemClickListener listener;


    public WeatherAdapter(Context context, List<RawWeatherData> weatherList, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.rawWeatherDataList = weatherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.modern_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        holder.bind(rawWeatherDataList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return rawWeatherDataList.size();
    }

    //set the data to the view

    public void refreshData(List<RawWeatherData> weatherList) {
        this.rawWeatherDataList = weatherList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTv;
        private ImageView statusIv;
        private TextView statusTv;
        private CardView statusCv;

        //constructor
        public ViewHolder(@NonNull View view) {
            super(view);
            timeTv = view.findViewById(R.id.tv_time);
            statusIv = view.findViewById(R.id.iv_status);
            statusTv = view.findViewById(R.id.tv_status);
            statusCv = view.findViewById(R.id.cv_status);
        }

        public void bind(RawWeatherData weather, final OnItemClickListener listener) {
            timeTv.setText(Helpers.convertUnixToDate(mContext,weather.getTime()));
            bindIcon(weather);
            statusCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(weather);
                }
            });
        }

        private void bindIcon(RawWeatherData weather) {
            WeatherAnalyzer weatherAnalyzer = new WeatherAnalyzer(mContext,weather);
            Status status = weatherAnalyzer.checkAll();
            statusIv.setImageResource(Helpers.statusToIcon(status));
            statusTv.setText(Helpers.statusToString(status));
        }
    }
}

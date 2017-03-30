package com.example.urjaswitk.sunshineapp.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.urjaswitk.sunshineapp.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY  = 1;

    private boolean mUseTodayLayout = false;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high) + "/" +
                Utility.formatTemperature(mContext, low);
        return highLowStr;
    }

    public void setmUseTodayLayout(boolean useTodayLayout)
    {
        mUseTodayLayout = useTodayLayout;
        Log.e("Adapter : ", "usetoday = " + mUseTodayLayout);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout)? VIEW_TYPE_TODAY
                : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       View view = LayoutInflater.from(context).inflate(
                (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) ?
                        R.layout.list_item_forecast_today :
                        R.layout.list_item_forecast,
                parent, false);
       view.setTag(new ViewHolder(view));
       return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.iconView.setImageResource(R.mipmap.ic_launcher);
        int viewType = getItemViewType(cursor.getPosition());
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int iconId;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                // Get weather icon
                iconId = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                // Get weather icon
                iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                break;
            }
            default:iconId=-1;
        }
        Glide.with(mContext)
                .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                .crossFade()
                .error(iconId)
                .into(holder.iconView);

        holder.dateView.setText(Utility.getFriendlyDayString(context,
                cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));
        holder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));
        holder.highTempView.setText(Utility.formatTemperature(context,
                        cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP)));
        holder.lowTempview.setText(Utility.formatTemperature(context,
                        cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP)));
    }

    private static class ViewHolder
    {
        TextView dateView, descriptionView, highTempView, lowTempview;
        ImageView iconView;

        public ViewHolder(View view){
            lowTempview = (TextView)view.findViewById(R.id.list_item_low_textview);
            dateView = (TextView)view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            iconView = (ImageView)view.findViewById(R.id.list_item_icon);
            highTempView = (TextView)view.findViewById(R.id.list_item_high_textview);
        }
    }
}
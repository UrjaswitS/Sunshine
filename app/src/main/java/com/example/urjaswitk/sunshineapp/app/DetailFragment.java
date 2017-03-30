package com.example.urjaswitk.sunshineapp.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.urjaswitk.sunshineapp.app.data.WeatherContract;

/**
 * Created by UrJasWitK on 13-Mar-17.
 */

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment
            .class.getCanonicalName();
    private static final String FORECAST_SHARE_HASHTAG = " #SubshineApp";
    public static final int DETAIL_LOADER = 0;
    static final String DETAIL_URI = "URI";

    /**
     * Member variables for the screen views
     */
    private TextView mDateView, mDayView, mDescriptionView, mWindLabelView;
    private TextView mHighTempView, mLowTempView, mHumidityLabelView;
    private TextView mHumidityView, mWindView, mPressureView, mPressureLabelView;
    private ImageView mIconView;

    /**
     * Other member variables
     */
    String forecastStr, mForecast;
    private Uri mUri;
    ShareActionProvider mShareActionProvider;

    /**
     * Column names constants to be used to retrieve data
     * from the content provider
     */
    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME +
                    "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP =3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_CONDITION_ID = 8;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onLocationChanged(String newLocation){
        Uri uri = mUri;
        if (uri != null){
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    newLocation, date);
            getLoaderManager().restartLoader(DetailFragment.DETAIL_LOADER, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle args =getArguments();
        if (args != null)
            mUri = args.getParcelable(DETAIL_URI);

        View rootView = inflater.inflate(R.layout.fragment_detail,container, false);
        /*mDayView = (TextView)rootView.findViewById(R.id.detail_item_day_text_view);
        mDateView = (TextView)rootView.findViewById(R.id.detail_item_date_text_view);
        mDescriptionView = (TextView)rootView.findViewById(R.id.detail_item_desc_text_view);
        mHighTempView = (TextView)rootView.findViewById(R.id.detail_item_high_temp_text_view);
        mLowTempView = (TextView)rootView.findViewById(R.id.detail_item_low_temp_text_view);
        mIconView = (ImageView)rootView.findViewById(R.id.detail_item_icon);
        mPressureView = (TextView)rootView.findViewById(R.id.detail_item_pressure_text_view);
        mHumidityView = (TextView)rootView.findViewById(R.id.detail_item_humidity_text_view);
        mWindView = (TextView)rootView.findViewById(R.id.detail_item_wind_text_view);*/
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mHumidityLabelView = (TextView) rootView.findViewById(R.id.detail_humidity_label_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mWindLabelView = (TextView) rootView.findViewById(R.id.detail_wind_label_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        mPressureLabelView = (TextView) rootView.findViewById(R.id.detail_pressure_label_textview);


        return rootView;
    }

    public void doTheWork()
    {
        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    private Intent createShareForecastIntent(){
        return new Intent().setType("text/plain")
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .putExtra(Intent.EXTRA_TEXT,
                        forecastStr + FORECAST_SHARE_HASHTAG);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null)
            return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS,
                    null, null, null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst())
            return;
        int weatherId = data.getInt(DetailFragment.COL_WEATHER_CONDITION_ID);
        Log.d(LOG_TAG, "id : " + weatherId);
        updateUI(data, weatherId);
        if (mShareActionProvider != null)
            mShareActionProvider.setShareIntent(
                    createShareForecastIntent());
    }

    private void updateUI(Cursor cursor, int id){
//        mDayView.setText(Utility.getDayName(getContext(),
   //                     cursor.getLong(DetailFragment.COL_WEATHER_DATE)));
        mDateView.setText(Utility.getFormattedMonthDay(getContext(),
                        cursor.getLong(DetailFragment.COL_WEATHER_DATE)));
        mHighTempView.setText(Utility.formatTemperature(getContext(),
                        cursor.getDouble(DetailFragment.COL_WEATHER_MAX_TEMP)));
        mLowTempView.setText(Utility.formatTemperature(getContext(),
                        cursor.getDouble(DetailFragment.COL_WEATHER_MIN_TEMP)));
        Glide.with(this)
                .load(Utility.getArtUrlForWeatherCondition(getActivity(), id))
                .error(Utility.getArtResourceForWeatherCondition(id))
                .crossFade()
                .into(mIconView);
        mHumidityView.setText(cursor.getString(DetailFragment.COL_WEATHER_HUMIDITY) + " 84%");
        mPressureView.setText(cursor.getString(DetailFragment.COL_WEATHER_PRESSURE) + " hPa");
        mDescriptionView.setText(cursor.getString(DetailFragment.COL_WEATHER_DESC));
        mWindView.setText(cursor.getString(DetailFragment.COL_WEATHER_WIND_SPEED) + " Km/H NW");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

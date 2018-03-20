package com.cryptospective.marco.cscpricewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.BACKGROUND_ALPHA;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.COLOR_DEFAULT_BACKGROUND;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.COLOR_DEFAULT_TEXT;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.REFRESH_WIDGET;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.WIDGET_BACKGROUND_COLOR;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.WIDGET_CURRENCY;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.WIDGET_ICON;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.WIDGET_PREFS;
import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.WIDGET_TEXT_COLOR;


public class UpdateWidgetServiceMain extends Service {
    public int[] widgetIds;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            try {
                widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
            } catch (Exception e) {
                widgetIds = new int[]{intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)};
            }
        }

        if (widgetIds == null) {

            ComponentName thisWidget = new ComponentName(this, MobileAssistantWidgetMain.class);
            widgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(thisWidget);
        }
        for (int widgetId : widgetIds) {


            ChangeWidgetThread changeWidgetThread = new ChangeWidgetThread(widgetId);
            changeWidgetThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    public void changeWidget(String priceUSD, String priceBTC, final int widgetId) {

        showProgressBar(false, widgetId);


        SharedPreferences mSharedPreferences = getSharedPreferences(WIDGET_PREFS + widgetId, Context.MODE_MULTI_PROCESS);
        String mFontSelectedColor = mSharedPreferences.getString(WIDGET_TEXT_COLOR, COLOR_DEFAULT_TEXT);
        String mPanelSelectedColor = mSharedPreferences.getString(WIDGET_BACKGROUND_COLOR, COLOR_DEFAULT_BACKGROUND);
        String mCurrency = mSharedPreferences.getString(WIDGET_CURRENCY, "0");
        String mIcon = mSharedPreferences.getString(WIDGET_ICON, "0");

        final RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

        if (mCurrency.equals("0")) {
            remoteView.setTextViewText(R.id.price, "฿" + priceBTC);
        } else {
            remoteView.setTextViewText(R.id.price, "$" + priceUSD);
        }


        if(mIcon.equals("0")){
            remoteView.setInt(R.id.icon, "setImageResource", R.drawable.icon);
        }else if(mIcon.equals("1")){
            remoteView.setInt(R.id.icon, "setImageResource", R.drawable.icon_white);
        }else{
            remoteView.setInt(R.id.icon, "setVisibility", View.GONE);
        }

        remoteView.setTextColor(R.id.price, Color.parseColor(mFontSelectedColor));

        remoteView.setInt(R.id.background, "setColorFilter", Color.parseColor(mPanelSelectedColor));
        remoteView.setInt(R.id.background, "setAlpha", mSharedPreferences.getInt(BACKGROUND_ALPHA, 255));

        AppWidgetManager.getInstance(this).partiallyUpdateAppWidget(widgetId, remoteView);

        Intent refreshWidget = new Intent(this.getApplicationContext(), MobileAssistantWidgetMain.class);
        refreshWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        refreshWidget.setAction(REFRESH_WIDGET);
        refreshWidget.setData(Uri.parse(refreshWidget.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent pendingRefresh = PendingIntent.getBroadcast(this.getApplicationContext(), 0, refreshWidget, 0);
        remoteView.setOnClickPendingIntent(R.id.widget, pendingRefresh);
        AppWidgetManager.getInstance(this).partiallyUpdateAppWidget(widgetId, remoteView);


    }



    public void showProgressBar(boolean isShow, int widgetId) {
        RemoteViews remoteView = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
        remoteView.setViewVisibility(R.id.progressBar, (isShow ? View.VISIBLE : View.GONE));
        remoteView.setViewVisibility(R.id.icon, (!isShow ? View.VISIBLE : View.GONE));
        remoteView.setViewVisibility(R.id.price, (!isShow ? View.VISIBLE : View.GONE));
        AppWidgetManager.getInstance(this).partiallyUpdateAppWidget(widgetId, remoteView);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ChangeWidgetThread extends Thread {
        private int widgetId;

        ChangeWidgetThread(int widgetId) {
            this.widgetId = widgetId;
        }

        @Override
        public void run() {
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

                UpdateWidgetServiceMain.this.showProgressBar(true, widgetId);

                Request.getInstance().getRequest("v1/ticker/casinocoin", new Request.IRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.optJSONObject(0);

                            changeWidget(jsonObject.optString("price_usd"), jsonObject.optString("price_btc"), widgetId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String message) {

                    }
                });
            }

        }
    }
}
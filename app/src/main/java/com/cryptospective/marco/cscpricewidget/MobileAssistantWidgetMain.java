package com.cryptospective.marco.cscpricewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;


public class MobileAssistantWidgetMain extends AppWidgetProvider {
    public static final String EDIT_WIDGET = "edit widget click";
    public static final String WIDGET_PREFS = "Widget_Prefs_";

    public static final String WIDGET_TEXT_COLOR = "widget_text_color";
    public static final String WIDGET_BACKGROUND_COLOR = "widget_background_color";
    public static final String COLOR_DEFAULT_TEXT = "#bf0a0a";
    public static final String BACKGROUND_ALPHA = "alpha";
    public static final String COLOR_DEFAULT_BACKGROUND = "#c4000000";

    public static final String WIDGET_ICON = "widget_icon";
    public static final String WIDGET_CURRENCY = "widget_currency";

    public static final String REFRESH_WIDGET = "refresh widget";


    @Override
    public void onReceive(Context context, Intent i) {
        super.onReceive(context, i);
        Bundle extras = i.getExtras();
        if (extras == null) {
            return;
        }
        int widgetId = extras
                .getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            if (i.getAction().equals(REFRESH_WIDGET)) {
                this.onUpdate(context, AppWidgetManager.getInstance(context), new int[]{widgetId});
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int[] widgetSizes = new int[appWidgetIds.length];

        Intent refresh = new Intent(context, UpdateWidgetServiceMain.class);
        refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds);
        refresh.setData(Uri.parse(refresh.toUri(Intent.URI_INTENT_SCHEME)));

        for(int i = 0; i < appWidgetIds.length; i++){
            widgetSizes[i] = appWidgetManager.getAppWidgetOptions(appWidgetIds[i]).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        }
        refresh.putExtra("SIZE_WIDGETS", widgetSizes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(refresh);
        } else {
            context.startService(refresh);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        float newSize = (maxWidth - 84) / 6;
        remoteViews.setTextViewTextSize(R.id.price, TypedValue.COMPLEX_UNIT_SP, newSize);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);


    }
}
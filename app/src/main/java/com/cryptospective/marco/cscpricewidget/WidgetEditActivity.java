package com.cryptospective.marco.cscpricewidget;

import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caladbolg.Caladbolg;
import com.caladbolg.utils.ColorUtils;

import java.util.Random;

import static com.cryptospective.marco.cscpricewidget.MobileAssistantWidgetMain.*;

public class WidgetEditActivity extends AppCompatActivity implements Caladbolg.ColorPickerCallback {

    private String mFontSelectedColor;
    private String mPanelSelectedColor;

    View BTC, USD, iconOne, iconTwo, noIcon, save, xxx, spade;
    TextView fontColor, panelColor;

    SharedPreferences mSharedPreferences;
    private int mWidgetId;
    boolean isFontColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_edit);

        BTC = findViewById(R.id.BTC);
        USD = findViewById(R.id.USD);
        iconOne = findViewById(R.id.iconOne);
        iconTwo = findViewById(R.id.iconTwo);
        noIcon = findViewById(R.id.noIcon);
        save = findViewById(R.id.save);
        fontColor = findViewById(R.id.fontColor);
        panelColor = findViewById(R.id.panelColor);
        xxx = findViewById(R.id.xxx);
        spade = findViewById(R.id.spade);

        xxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard();
            }
        });

        BTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTC.setBackgroundResource(R.color.select);
                USD.setBackgroundResource(R.color.mainBackground);
                mSharedPreferences.edit().putString(WIDGET_CURRENCY, "0").apply();
            }
        });
        USD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                USD.setBackgroundResource(R.color.select);
                BTC.setBackgroundResource(R.color.mainBackground);
                mSharedPreferences.edit().putString(WIDGET_CURRENCY, "1").apply();
            }
        });
        iconOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconOne.setBackgroundResource(R.color.select);
                iconTwo.setBackgroundResource(R.color.mainBackground);
                noIcon.setBackgroundResource(R.color.mainBackground);
                mSharedPreferences.edit().putString(WIDGET_ICON, "0").apply();
            }
        });
        iconTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconTwo.setBackgroundResource(R.color.select);
                iconOne.setBackgroundResource(R.color.mainBackground);
                noIcon.setBackgroundResource(R.color.mainBackground);
                mSharedPreferences.edit().putString(WIDGET_ICON, "1").apply();
            }
        });
        noIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noIcon.setBackgroundResource(R.color.select);
                iconOne.setBackgroundResource(R.color.mainBackground);
                iconTwo.setBackgroundResource(R.color.mainBackground);
                mSharedPreferences.edit().putString(WIDGET_ICON, "2").apply();
            }
        });


        fontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("caladbolg");
                FragmentTransaction ft = fm.beginTransaction();

                if (prev == null) {
                    Caladbolg mCaladbolg = Caladbolg.newInstance(Color.parseColor(mFontSelectedColor));
                    ft.add(mCaladbolg, "caladbolg");
                } else {
                    ft.show(prev);
                }
                isFontColor = true;
                ft.commit();
            }
        });

        panelColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getSupportFragmentManager();
                Fragment prev = fm.findFragmentByTag("caladbolg");
                FragmentTransaction ft = fm.beginTransaction();

                if (prev == null) {
                    Caladbolg mCaladbolg = Caladbolg.newInstance(Color.parseColor(mPanelSelectedColor));
                    ft.add(mCaladbolg, "caladbolg");
                } else {
                    ft.show(prev);
                }
                isFontColor = false;
                ft.commit();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
                setResult(RESULT_OK, resultValue);


                Intent refresh = new Intent(WidgetEditActivity.this, MobileAssistantWidgetMain.class);
                refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
                refresh.setAction(REFRESH_WIDGET);
                sendBroadcast(refresh);

                finish();
            }
        });

        spade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random rand = new Random();

                mSharedPreferences.edit().putString(WIDGET_CURRENCY, "" + rand.nextInt(2)).apply();
                mSharedPreferences.edit().putString(WIDGET_ICON, "" + rand.nextInt(3)).apply();


                mSharedPreferences.edit().putString(WIDGET_TEXT_COLOR, generateColor(rand)).apply();
                mSharedPreferences.edit().putString(WIDGET_BACKGROUND_COLOR, generateColor(rand)).apply();


                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
                setResult(RESULT_OK, resultValue);


                Intent refresh = new Intent(WidgetEditActivity.this, MobileAssistantWidgetMain.class);
                refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
                refresh.setAction(REFRESH_WIDGET);
                sendBroadcast(refresh);

                finish();
            }
        });


        if (!getIntent().getAction().equals(EDIT_WIDGET)) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
            }

        } else {
            mWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        }

        mSharedPreferences = getSharedPreferences(WIDGET_PREFS + mWidgetId, Context.MODE_MULTI_PROCESS);
        mFontSelectedColor = mSharedPreferences.getString(WIDGET_TEXT_COLOR, COLOR_DEFAULT_TEXT);
        mPanelSelectedColor = mSharedPreferences.getString(WIDGET_BACKGROUND_COLOR, COLOR_DEFAULT_BACKGROUND);

    }

    @Override
    public void onPickColor(int rgb, int alpha, int listElement) {
        if (isFontColor) {
            mFontSelectedColor = ColorHelper.checkColor(ColorUtils.colorCode(ColorUtils.argb(rgb, alpha)));
            fontColor.setTextColor(Color.parseColor(mFontSelectedColor));
            mSharedPreferences.edit().putString(WIDGET_TEXT_COLOR, mFontSelectedColor).apply();
        } else {
            mPanelSelectedColor = ColorHelper.checkColor(ColorUtils.colorCode(ColorUtils.argb(rgb, alpha)));
            panelColor.setTextColor(Color.parseColor(mPanelSelectedColor));
            mSharedPreferences.edit().putString(WIDGET_BACKGROUND_COLOR, mPanelSelectedColor).apply();
        }
    }

    @Override
    public void onCancel() {

    }

    public void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("beer", "cKxv2YDfVRgcX9eVQBRtfVwwEEKgofvKjH");
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(this, "Copied", Toast.LENGTH_LONG);
        View view = toast.getView();
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setTextSize(20f);
        text.setShadowLayer(0,0,0,0);
        text.setBackgroundResource(R.color.transparent);
        toast.show();
    }

    private static String generateColor(Random r) {
        final char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char [] s = new char[7];
        int     n = r.nextInt(0x1000000);

        s[0] = '#';
        for (int i=1;i<7;i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        return new String(s);
    }
}

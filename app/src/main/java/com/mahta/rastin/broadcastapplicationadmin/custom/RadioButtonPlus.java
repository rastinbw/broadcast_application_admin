package com.mahta.rastin.broadcastapplicationadmin.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.G;

public class RadioButtonPlus extends android.support.v7.widget.AppCompatRadioButton {

    public RadioButtonPlus(Context context) {
        super(context);
    }

    public RadioButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context , attrs);
    }

    public RadioButtonPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomFont(context , attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.RadioButtonPlus);
        String customFont = a.getString(R.styleable.RadioButtonPlus_fontRadioButton);

        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {

        Typeface tf;

        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/"+asset);
        } catch (Exception e) {
            G.e("Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}


package com.mahta.rastin.broadcastapplicationadmin.custom;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.global.G;

public class ButtonPlus extends android.support.v7.widget.AppCompatButton {

    public ButtonPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    public ButtonPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ButtonPlus(Context context) {
        super(context);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {

        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ButtonPlus);
        String customFont = a.getString(R.styleable.ButtonPlus_fontButton);

        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {

        Typeface tf;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/"+asset);
        } catch (Exception e) {
            G.e("Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}

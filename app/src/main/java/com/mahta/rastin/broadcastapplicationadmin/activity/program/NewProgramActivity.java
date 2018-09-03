package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;

public class NewProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextPlus edtTitle, edtPreview;

    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private SliderPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_new);

        findViewById(R.id.imgBack).setOnClickListener(this);
        findViewById(R.id.txtApply).setOnClickListener(this);
        findViewById(R.id.txtBack).setOnClickListener(this);

        edtTitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        viewPager = findViewById(R.id.view_pager);
        layoutDots = findViewById(R.id.layoutDots);

        pagerAdapter = new SliderPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        showDotes(viewPager.getCurrentItem());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                showDotes(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void showDotes(int pageNumber) {

        TextView dots[] = new TextView[viewPager.getAdapter().getCount()];

        layoutDots.removeAllViews();

        for (int i = 0 ; i < dots.length ; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));

            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP , 35);
            dots[i].setTextColor(ContextCompat.getColor(this,
                    (i == pageNumber)? R.color.dot_active : R.color.dot_inactive));

            layoutDots.addView(dots[i]);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.imgBack || id == R.id.txtBack) {
            finish();

        } else if (id == R.id.txtApply) {


            String title = edtTitle.getText().toString().trim();
            String preview = edtPreview.getText().toString().trim();
            String content = "";
            int groupId = 2;


            ContentValues contentValues = new ContentValues();

            contentValues.put("token", RealmController.getInstance().getUserToken().getToken());
            contentValues.put("title", title);
            contentValues.put("preview_content", preview);
            contentValues.put("content", content);

            new HttpCommand(HttpCommand.COMMAND_CREATE_POST, contentValues).setOnResultListener(new OnResultListener() {
                @Override
                public void onResult(String result) {

                    if (JSONParser.getResultCodeFromJson(result) == 1000){

                        Toast.makeText(NewProgramActivity.this, "اطلاعیه جدید افزوده شد", Toast.LENGTH_SHORT).show();
                        finish();
                    }


                }
            }).execute();


        }

    }

    public class SliderPagerAdapter extends PagerAdapter {

        String[] slideTitles;

        public SliderPagerAdapter() {

            slideTitles = getResources().getStringArray(R.array.slide_titles);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View view = LayoutInflater.from(NewProgramActivity.this)
                    .inflate(R.layout.layout_program_slide , container , false);


            ((TextView) findViewById(R.id.slide_title)).setText(slideTitles[position]);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return slideTitles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            View view = (View) object;
            container.removeView(view);
        }
    }
}

package com.mahta.rastin.broadcastapplicationadmin.activity.program;

import android.support.design.widget.TabLayout;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.EditTextPlus;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

public class EditProgramActivity extends AppCompatActivity implements View.OnClickListener {

    private Program program;
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

        program = getIntent().getParcelableExtra(Keys.KEY_EXTRA_FLAG);

        edtTitle = findViewById(R.id.edt_title);
        edtPreview = findViewById(R.id.edt_preview);

        edtTitle.setText(program.getTitle());
        edtPreview.setText(program.getPreview());


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
            Toast.makeText(this, "ثبت", Toast.LENGTH_SHORT).show();
        }

    }


    public class SliderPagerAdapter extends PagerAdapter {

        String[] slideTitles;

        public SliderPagerAdapter() {

            slideTitles = getResources().getStringArray(R.array.slide_titles);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = LayoutInflater.from(EditProgramActivity.this)
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

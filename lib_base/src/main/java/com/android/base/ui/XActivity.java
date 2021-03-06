package com.android.base.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.base.BuildConfig;
import com.android.base.R;
import com.android.base.SharedPreferencesUtil;
import com.android.base.language.LanguagesManager;
import com.android.base.KeyboardUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

public abstract class XActivity<P extends IPresent> extends SupportActivity implements IView<P> {

    private P p;
    protected Activity context;
    protected TextView tv_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        int theme = (int) SharedPreferencesUtil.getData("theme", 0);
        if (theme == 0) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);
        context = this;
        try {
            setContentView(getLayoutId());

            View view = findViewById(R.id.iv_back);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            tv_title = findViewById(R.id.tv_title);
            int activityTitle = getActivityTitle();
            if (tv_title != null && activityTitle != 0) {
                tv_title.setText(getResources().getString(activityTitle));
            }

            bindUI(null);
            initData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getActivityTitle() {
        return 0;
    }


    @Override
    public void bindUI(View rootView) {

    }

    @Override
    public Resources getResources() {
        return LanguagesManager.getAppLanguageResources(this);
    }

    protected P getP() {
        if (p == null) {
            p = newP();
            if (p != null) {
                p.attachV(this);
            }
        }
        return p;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getP() != null) {
            getP().detachV();
        }
        p = null;
    }

    @Override
    public P newP() {
        return null;
    }

    @Override
    public void finish() {
        super.finish();
        KeyboardUtils.hideSoftInput(this);
    }

    //region??????????????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isTouchView(filterViewByIds(), ev)) return super.dispatchTouchEvent(ev);
            if (hideSoftByEditViewIds() == null || hideSoftByEditViewIds().length == 0)
                return super.dispatchTouchEvent(ev);
            View v = getCurrentFocus();
            if (isFocusEditText(v, hideSoftByEditViewIds())) {
                if (isTouchView(hideSoftByEditViewIds(), ev))
                    return super.dispatchTouchEvent(ev);
                //????????????
                KeyboardUtils.hideSoftInput(this);
                clearViewFocus(v, hideSoftByEditViewIds());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ??????editText?????????
     *
     * @param v   ????????????View
     * @param ids ?????????
     */
    protected void clearViewFocus(View v, int... ids) {
        if (null != v && null != ids && ids.length > 0) {
            for (int id : ids) {
                if (v.getId() == id) {
                    v.clearFocus();
                    break;
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param v   ????????????View
     * @param ids ?????????
     * @return true???????????????edit???
     */
    protected boolean isFocusEditText(View v, int... ids) {
        if (v instanceof EditText) {
            EditText tmp_et = (EditText) v;
            for (int id : ids) {
                if (tmp_et.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    //?????????????????????view??????,?????????????????????
    protected boolean isTouchView(View[] views, MotionEvent ev) {
        if (views == null || views.length == 0) return false;
        int[] location = new int[2];
        for (View view : views) {
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth())
                    && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    //?????????????????????view??????,?????????????????????
    protected boolean isTouchView(int[] ids, MotionEvent ev) {
        int[] location = new int[2];
        for (int id : ids) {
            View view = findViewById(id);
            if (view == null) continue;
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() > x && ev.getX() < (x + view.getWidth())
                    && ev.getY() > y && ev.getY() < (y + view.getHeight())) {
                return true;
            }
        }
        return false;
    }

    /**
     * ??????EditText???Id
     * ???????????????EditText????????????
     *
     * @return id ??????
     */
    protected int[] hideSoftByEditViewIds() {
        return null;
    }

    /**
     * ??????????????????View
     * ??????????????????????????????????????????????????????
     *
     * @return id ??????
     */
    protected View[] filterViewByIds() {
        return null;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        addDebugTextView();
    }

    /***
     * 2018.8.8
     * ????????????,?????????????????????
     */
    protected void addDebugTextView() {
        if (BuildConfig.DEBUG) {
            //????????????TextView,?????????????????????Activity???
            View decorView = getWindow().getDecorView();
            if (decorView instanceof FrameLayout) {
                TextView textView = new TextView(this);
                textView.setTextSize(9);
                textView.setTextColor(Color.WHITE);
                float dp2 = 3;
                int padding = (int) dp2 * 4;
                textView.setPadding(padding, padding, padding, padding);
                textView.setShadowLayer(dp2 * 2, dp2, dp2, Color.BLACK);
                textView.setText(this.getClass().getSimpleName());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
                layoutParams.gravity = Gravity.BOTTOM;
                if (decorView.getBottom() > getResources().getDisplayMetrics().heightPixels) {
                    //??????????????????
                    Resources resources = getResources();
                    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                    int navBarHeight = 0;
                    if (resourceId > 0) {
                        navBarHeight = resources.getDimensionPixelSize(resourceId);
                    }
                    layoutParams.bottomMargin = navBarHeight;
                }
                ((ViewGroup) decorView).addView(textView, layoutParams);
            }
        }
    }
}

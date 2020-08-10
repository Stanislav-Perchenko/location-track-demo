package com.alperez.location.demo.base;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alperez.location.demo.R;
import com.alperez.utils.ViewUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by stanislav.perchenko on 11/13/2019, 12:30 PM.
 */
public abstract class BaseToolbarActivity extends AppCompatActivity implements ISoftKeyboardController {

    @Nullable
    protected abstract Integer getToolbarResId();

    @Nullable
    protected abstract String getScreenTitle();

    @Nullable
    protected abstract String getScreenSubTitle();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!setupContentView()) {
            throw new IllegalStateException("The 'Layout' annotation is not provided for the child Activity");
        }
        setupToolbar();
    }

    private boolean setupContentView() {
        Class cls = getClass();
        if (cls.isAnnotationPresent(Layout.class)) {
            setContentView(((Layout) cls.getAnnotation(Layout.class)).value());
            return true;
        } else {
            return false;
        }
    }

    private boolean setupToolbar() {
        Integer resId = getToolbarResId();
        if (resId != null) {
            Toolbar vTb = findViewById(resId.intValue());
            vTb.setTitleTextColor(getColor(R.color.text_white));
            setSupportActionBar(vTb);
            ActionBar ab = getSupportActionBar();
            String title = getScreenTitle();
            if (title != null) ab.setTitle(title);
            String subtitle = getScreenSubTitle();
            if (subtitle != null) ab.setSubtitle(subtitle);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void hideSoftwareKeyboard() {
        ViewUtils.hideSoftwareKeyboard(this);
    }

    @Override
    public void showSoftwareKeyboard(View v) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, 0);
    }


    private final int[] view_location = {0, 0};

    private boolean isPointInsideView(View v, int px, int py) {
        v.getLocationOnScreen(view_location);
        int left = view_location[0];
        int top  = view_location[1];

        if (px < left || py < top || px >= (left + v.getWidth()) || py >= (top + v.getHeight())) {
            return false;
        } else {
            return true;
        }
    }

    private final List<View> vvv = new LinkedList<>();

    public void addViewToSuppressHideKeyboard(View supressView) {
        for (View v : vvv) {
            if (v == supressView) return;
        }
        vvv.add(supressView);
    }

    public boolean removeViewToSuppressHideKeyboard(View supressView) {
        for (Iterator<View> itr = vvv.iterator(); itr.hasNext(); ) {
            View v = itr.next();
            if (v == supressView) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            View currentView = getCurrentFocus();
            if (currentView instanceof EditText) {
                final int px = (int) event.getRawX();
                final int py = (int) event.getRawY();
                boolean isPointerOk = isPointInsideView(currentView, px, py);
                if (!isPointerOk) {
                    for (View exclView : vvv) {
                        if (isPointerOk = isPointInsideView(exclView, px, py)) break;
                    }
                }

                if (!isPointerOk) {
                    hideSoftwareKeyboard();
                    getWindow().getDecorView().requestFocus(View.FOCUS_DOWN);
                }
            }
        }
        return result;
    }
}

package com.alperez.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alperez.location.demo.R;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Created by stanislav.perchenko on 10/22/2019.
 */
public final class ViewUtils {
    public static float dp2px(Resources res, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    @SuppressLint("NewApi")
    public static int getColorFromResourcesCompat(Resources res, int resId, @Nullable Resources.Theme theme) {
        return (Build.VERSION.SDK_INT >= 23) ? res.getColor(resId, theme) : res.getColor(resId);
    }

    @SuppressLint("NewApi")
    public static Drawable getDrawableForResource(Resources res, int resId, @Nullable Resources.Theme theme) {
        return (Build.VERSION.SDK_INT >= 21) ? res.getDrawable(resId, theme) : res.getDrawable(resId);
    }

    public static int getStatusBarHeight(Resources res) {
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId > 0) ? res.getDimensionPixelSize(resourceId) : res.getDimensionPixelSize(R.dimen.stat_bar_fallback_height);
    }

    public static int getNavigationBarHeight(Resources res) {
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        return (resourceId > 0) ? res.getDimensionPixelSize(resourceId) : res.getDimensionPixelSize(R.dimen.nav_bar_fallback_height);
    }

    public static int getActionBarHeight(Context ctx) {
        Resources res = ctx.getResources();
        int h = 0;
        try {
            int resId = res.getIdentifier("action_bar_default_height", "dimen", "android");
            if (resId > 0) {
                h = res.getDimensionPixelSize(resId);
            }
            TypedValue tVal = new TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.actionBarSize, tVal, true);
            if (tVal.type == TypedValue.TYPE_DIMENSION) {
                int h1 = TypedValue.complexToDimensionPixelSize(tVal.data, res.getDisplayMetrics());
                if (h1 > h) {
                    h = h1;
                }
            }
            return h;
        } finally {
            if (h == 0) {
                return res.getDimensionPixelSize(R.dimen.action_bar_fallback_height);
            }
        }
    }

    public static void hideSoftwareKeyboard(Activity act) {
        View v = act.getWindow().getCurrentFocus();
        if (v == null) {
            v = act.getWindow().getDecorView();
        }

        if (v != null) {
            IBinder w_tok = v.getWindowToken();
            if (w_tok != null) {
                ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(w_tok, 0);
            }
        }
    }

    public static void showSoftwareKeyboard(Context ctx, View v) {
        ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(v, 0);
    }

    public static void setTextNullProtected(TextView tv, @Nullable String text) {
        tv.setText((text == null) ? "" : text);
    }

    public static TextView setTextViewDrawableColor(TextView tv, int color) {
        for (Drawable drawable : tv.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
        return tv;
    }

    public static View applyColorToBackground(View v, int color) {
        if (v.getBackground() != null) {
            v.getBackground().setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        return v;
    }

    @Nullable
    public static <T extends TextView> TextInputLayout findParentTextInputLayout(@NonNull T textView) {
        ViewParent parent = textView.getParent();
        do {
            if (parent instanceof TextInputLayout) return (TextInputLayout) parent;
        } while ((parent = parent.getParent()) != null);
        return null;
    }

    private ViewUtils() { }
}

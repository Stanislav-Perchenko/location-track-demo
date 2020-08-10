package com.alperez.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.alperez.location.demo.DemoApplication;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by stanislav.perchenko on 10/22/2019.
 */
public final class Toaster {


    public static Toast toastToUser(int resId, int length) {
        Toast t = Toast.makeText(DemoApplication.getStaticAppContext(), resId, length);
        t.show();
        return t;
    }

    public static Toast toastToUser(CharSequence text, int length) {
        Toast t = Toast.makeText(DemoApplication.getStaticAppContext(), text, length);
        t.show();
        return t;
    }

    public static void showSnackbar(View container, final String text) {
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    public static Snackbar showSnackbar(View container, final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Context ctx = container.getContext();
        Snackbar sb = Snackbar.make(container, ctx.getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE)
                .setAction(ctx.getString(actionStringId), listener);
        sb.show();
        return sb;
    }

    private Toaster() { }


}


package com.vineSwipe.swipe.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

/**
 * Shortcuts relating to this app just being for code review.
 *
 * Created by alex on 06/10/15.
 */
public class StubHelper {
    public static void showYouBrokeItDialog(String message, @Nullable final Runnable onRetry, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Check your connection fellow giffer")
                .setMessage(message)
                .setNegativeButton("No Thanks", null);

        if (onRetry != null) {
            builder.setPositiveButton("Ok,I did ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onRetry.run();
                }
            });
        }

        builder.show();
    }

    public static void showYouBrokeItDialog(String message, Context context) {
        showYouBrokeItDialog(message, null, context);
    }
}

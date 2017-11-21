package ekta.com.movies_rxjava.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Ekta on 15-11-2017.
 */

public class Utils {

    private static Toast toast;

    //Set Methods
    public static void showToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 250);
        } else toast.setText(message);
        toast.show();
    }
}

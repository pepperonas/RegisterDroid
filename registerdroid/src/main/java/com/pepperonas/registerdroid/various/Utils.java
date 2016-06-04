package com.pepperonas.registerdroid.various;

import android.content.Context;

import com.pepperonas.registerdroid.R;
import com.pepperonas.jbasx.format.StringFormatUtils;
import com.pepperonas.jbasx.format.TimeFormatUtils;

/**
 * @author Martin Pfeffer, Sebastian Grätz
 */
public class Utils {

    public static String makePrintableTimeStamp(String ts) {return TimeFormatUtils.formatTime(Long.valueOf(ts), TimeFormatUtils.DEFAULT_FORMAT).replace("-", "/");}


    public static String formatTotal(Context ctx, int value) {return String.format(ctx.getString(R.string.total), StringFormatUtils.formatDecimalForcePrecision(value / 100f, 2));}


    public static String formatSingleValue(Context ctx, int value) {return String.format(ctx.getString(R.string.euro_format), StringFormatUtils.formatDecimalForcePrecision(value / 100f, 2));}
}

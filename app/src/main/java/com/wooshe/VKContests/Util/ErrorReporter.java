package com.wooshe.VKContests.Util;

import android.content.Context;

public final class ErrorReporter
{
    private ErrorReporter() {}

    public static void bindReporter(Context context)
    {
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.inContext(context));
    }

    public static void reportError(Context context, Throwable error)
    {
        ExceptionHandler.reportOnlyHandler(context).uncaughtException(Thread.currentThread(), error);
    }

}

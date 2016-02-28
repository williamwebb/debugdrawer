package com.jug6ernaut.debugdrawer.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by williamwebb on 2/27/16.
 */
public class ViewUtils {

	@SuppressWarnings({ "unchecked", "UnusedDeclaration" })
	@CheckResult
	public static <T extends View> T findById(@NonNull View view, @IdRes int id) {
		return (T) view.findViewById(id);
	}

	@SuppressWarnings({ "unchecked", "UnusedDeclaration" })
	@CheckResult
	public static <T extends View> T findById(@NonNull Activity activity, @IdRes int id) {
		return (T) activity.findViewById(id);
	}

	@SuppressWarnings({ "unchecked", "UnusedDeclaration" })
	@CheckResult
	public static <T extends View> T findById(@NonNull Dialog dialog, @IdRes int id) {
		return (T) dialog.findViewById(id);
	}

}

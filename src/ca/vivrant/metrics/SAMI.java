/*
 * Copyright (c) 2014 Silviu Matei
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package ca.vivrant.metrics;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class SAMI {
	public static final String LOGTAG = "SAMI";

	private static final Map<Context, SAMI> instances;

	static {
		instances = new HashMap<Context, SAMI>();
	}

	public static SAMI install(final Application application) {
		final SAMI sami = new SAMI(application);
		instances.put(application, sami);

		return sami;
	}

	public static SAMI get(final Context context) {
		return instances.get(context);
	}


	private final Application application;
	private final Map<Activity, Long> startTimes;


	private SAMI(final Application application) {
		this.application = application;
		this.startTimes = new HashMap<Activity, Long>();

		this.application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityStopped(final Activity activity) {
				final Long startTime = startTimes.get(activity);
				final long currentTime = System.currentTimeMillis();

				if (startTime != null && currentTime > startTime) {
					Log.v(LOGTAG, (currentTime - startTime) + " spent in " + activity.getClass().getSimpleName());
				}
			}

			@Override
			public void onActivityStarted(final Activity activity) {
				startTimes.put(activity, System.currentTimeMillis());
			}

			@Override
			public void onActivitySaveInstanceState(final Activity activity, final Bundle bundle) {}

			@Override
			public void onActivityResumed(final Activity activity) {}

			@Override
			public void onActivityPaused(final Activity activity) {}

			@Override
			public void onActivityDestroyed(final Activity activity) {}

			@Override
			public void onActivityCreated(final Activity activity, final Bundle bundle) {}
		});
	}


	public static abstract class OnClickListener
			implements android.view.View.OnClickListener {
		@Override
		public final void onClick(final View view) {
			final SAMI sami = SAMI.get(view.getContext().getApplicationContext());
			final boolean samiSuccess;

			if (sami != null) {
				Log.d(SAMI.LOGTAG, view.getId() + ".onClick");
				samiSuccess = true;
			} else {
				samiSuccess = false;
			}

			onClick(view, samiSuccess);
		}

		public abstract void onClick(final View view, final boolean samiSuccess);
	}
}

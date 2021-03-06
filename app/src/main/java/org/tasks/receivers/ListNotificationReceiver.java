package org.tasks.receivers;

import android.content.Context;
import android.content.Intent;

import com.todoroo.andlib.utility.AndroidUtilities;
import com.todoroo.astrid.api.Filter;

import org.tasks.Notifier;
import org.tasks.analytics.Tracker;
import org.tasks.analytics.Tracking;
import org.tasks.injection.BroadcastComponent;
import org.tasks.injection.InjectingBroadcastReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import timber.log.Timber;

public class ListNotificationReceiver extends InjectingBroadcastReceiver {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String EXTRA_FILTER_TITLE = "extra_filter_title";
    private static final String EXTRA_FILTER_QUERY = "extra_filter_query";
    private static final String EXTRA_FILTER_VALUES = "extra_filter_values";

    @Inject Notifier notifier;
    @Inject Tracker tracker;

    @Override
    public void onReceive(Context context, final Intent intent) {
        super.onReceive(context, intent);

        Timber.i("onReceive(%s, %s)", context, intent);

        tracker.reportEvent(Tracking.Events.LEGACY_TASKER_TRIGGER);

        executorService.execute(() -> {
            String title = intent.getStringExtra(EXTRA_FILTER_TITLE);
            String query = intent.getStringExtra(EXTRA_FILTER_QUERY);
            String extras = intent.getStringExtra(EXTRA_FILTER_VALUES);
            notifier.triggerFilterNotification(
                    new Filter(title, query, AndroidUtilities.contentValuesFromSerializedString(extras)));
        });
    }

    @Override
    protected void inject(BroadcastComponent component) {
        component.inject(this);
    }
}

package com.segment.analytics.android.integrations.countly;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.core.tests.BuildConfig;
import com.segment.analytics.integrations.AliasPayload;
import com.segment.analytics.integrations.GroupPayload;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import java.util.HashMap;
import ly.count.android.sdk.Countly;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.roboelectric.*", "android.*" })
public class CountlyTest {
  @Mock Countly countly;
  @Mock Application context;
  @Mock Analytics analytics;
  Logger logger;
  CountlyIntegration integration;

  @Before public void setUp() {
    initMocks(this);
    logger = Logger.with(Analytics.LogLevel.DEBUG);
    integration = new CountlyIntegration(countly, logger, context, "https://countly.com", "foo");
    verify(countly).setLoggingEnabled(false);
    verify(countly).init(context, "https://countly.com", "foo");
  }

  @Test public void activityCreate() {
    Activity activity = mock(Activity.class);
    Bundle bundle = mock(Bundle.class);
    integration.onActivityCreated(activity, bundle);
    verifyNoMoreInteractions(countly);
  }

  @Test public void activityStart() {
    Activity activity = mock(Activity.class);
    integration.onActivityStarted(activity);
    verify(countly).onStart(activity);
  }

  @Test public void activityResume() {
    Activity activity = mock(Activity.class);
    integration.onActivityResumed(activity);
    verifyNoMoreInteractions(countly);
  }

  @Test public void activityPause() {
    Activity activity = mock(Activity.class);
    integration.onActivityPaused(activity);
    verifyNoMoreInteractions(countly);
  }

  @Test public void activityStop() {
    Activity activity = mock(Activity.class);
    integration.onActivityStopped(activity);
    verify(countly).onStop();
  }

  @Test public void activitySaveInstance() {
    Activity activity = mock(Activity.class);
    Bundle bundle = mock(Bundle.class);
    integration.onActivitySaveInstanceState(activity, bundle);
    verifyNoMoreInteractions(countly);
  }

  @Test public void activityDestroy() {
    Activity activity = mock(Activity.class);
    integration.onActivityDestroyed(activity);
    verifyNoMoreInteractions(countly);
  }

  @Test public void identify() {
    integration.identify(mock(IdentifyPayload.class));
    verifyNoMoreInteractions(countly);
  }

  @Test public void group() {
    integration.group(mock(GroupPayload.class));
    verifyNoMoreInteractions(countly);
  }

  @Test public void track() {
    integration.track(new TrackPayloadBuilder() //
        .event("foo") //
        .build());
    verify(countly).recordEvent("foo", new HashMap<String, String>(), 1, 0.0);

    Properties properties = new Properties().putValue("count", 10).putValue("sum", 20);
    integration.track(new TrackPayloadBuilder().event("bar").properties(properties).build());
    verify(countly).recordEvent("bar", properties.toStringMap(), 10, 20);
  }

  @Test public void alias() {
    integration.alias(mock(AliasPayload.class));
    verifyNoMoreInteractions(countly);
  }

  @Test public void screen() {
    integration.screen(new ScreenPayloadBuilder().category("foo").build());
    verify(countly).recordEvent("Viewed foo Screen", new HashMap<String, String>(), 1, 0.0);

    Properties properties = new Properties().putValue("count", 10).putValue("sum", 20);
    integration.screen(new ScreenPayloadBuilder().name("bar").properties(properties).build());
    verify(countly).recordEvent("Viewed bar Screen", properties.toStringMap(), 10, 20.0);
  }

  @Test public void flush() {
    integration.flush();
    verifyNoMoreInteractions(countly);
  }

  @Test public void reset() {
    integration.reset();
    verifyNoMoreInteractions(countly);
  }
}

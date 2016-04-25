package com.segment.analytics.android.integrations.countly;

import android.app.Activity;
import android.content.Context;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.Logger;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import java.util.Map;
import ly.count.android.sdk.Countly;

import static com.segment.analytics.Analytics.LogLevel.INFO;
import static com.segment.analytics.Analytics.LogLevel.VERBOSE;

/**
 * Countly is a general-purpose analytics tool for your mobile apps, with reports like traffic
 * sources, demographics, event tracking and segmentation.
 *
 * @see <a href="https://count.ly/">Countly</a>
 * @see <a href="https://segment.com/docs/integrations/countly/">Countly Integration</a>
 * @see <a href="https://github.com/Countly/countly-sdk-android">Countly Android SDK</a>
 */
public class CountlyIntegration extends Integration<Countly> {
  public static final Factory FACTORY = new Factory() {
    @Override public Integration<?> create(ValueMap settings, Analytics analytics) {
      String serverUrl = settings.getString("serverUrl");
      String appKey = settings.getString("appKey");
      return new CountlyIntegration(Countly.sharedInstance(), analytics.logger(COUNTLY_KEY),
          analytics.getApplication(), serverUrl, appKey);
    }

    @Override public String key() {
      return COUNTLY_KEY;
    }
  };
  private static final String VIEWED_EVENT_FORMAT = "Viewed %s Screen";
  private static final String COUNTLY_KEY = "Countly";

  final Countly countly;
  final Logger logger;

  CountlyIntegration(Countly countly, Logger logger, Context context, String serverUrl,
      String appKey) throws IllegalStateException {
    this.countly = countly;
    this.logger = logger;
    boolean loggingEnabled = logger.logLevel == INFO || logger.logLevel == VERBOSE;
    countly.setLoggingEnabled(loggingEnabled);
    logger.verbose("countly.setLoggingEnabled(%s)", loggingEnabled);
    countly.init(context, serverUrl, appKey);
    logger.verbose("countly.init(context, %s, %s)", serverUrl, appKey);
  }

  @Override public Countly getUnderlyingInstance() {
    return countly;
  }

  @Override public void onActivityStarted(Activity activity) {
    super.onActivityStarted(activity);
    countly.onStart(activity);
    logger.verbose("countly.onStart(activity)");
  }

  @Override public void onActivityStopped(Activity activity) {
    super.onActivityStopped(activity);
    countly.onStop();
    logger.verbose("countly.onStop()");
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    event(track.event(), track.properties());
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    event(String.format(VIEWED_EVENT_FORMAT, screen.event()), screen.properties());
  }

  private void event(String name, Properties properties) {
    int count = properties.getInt("count", 1);
    double sum = properties.getDouble("sum", 0);
    Map<String, String> segmentation = properties.toStringMap();
    countly.recordEvent(name, segmentation, count, sum);
    logger.verbose("countly.recordEvent(%s, %s, %s, %s)", name, segmentation, count, sum);
  }
}

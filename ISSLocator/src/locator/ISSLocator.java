package locator;

import net.iakovlev.timeshape.TimeZoneEngine;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class ISSLocator {
  private static final TimeZoneEngine TIME_ZONE_ENGINE =
    TimeZoneEngine.initialize();
  private static final SimpleDateFormat DEFAULT_DATE_FORMATTER = 
    new SimpleDateFormat("MMMM, d, yyyy, HH:mm:ss z");

  private final ISSWebService issWebService;

  public ISSLocator(ISSWebService service) {
    issWebService = service;
  }

  String convertTimeStampToUTC(long secondsSinceEpoch) {
    ISSLocator.DEFAULT_DATE_FORMATTER
      .setTimeZone(TimeZone.getTimeZone("UTC"));

    return ISSLocator.DEFAULT_DATE_FORMATTER.format(
      new Date(secondsSinceEpoch * 1000L));
  }

  String convertTimeStampToTimeAtLatLon(
    long timeStampSeconds, double lat, double lon) {
    
    final ZoneId zoneId = ISSLocator.TIME_ZONE_ENGINE.query(lat, lon).get();
    ISSLocator.DEFAULT_DATE_FORMATTER
      .setTimeZone(TimeZone.getTimeZone(zoneId));

    return ISSLocator.DEFAULT_DATE_FORMATTER.format(
      new Date(timeStampSeconds * 1000L));
  }

  public String computeTimeOfFlyOver(double lat, double lon) {
    try {
      long timeStamp = issWebService.fetchISSFlyOverData(lat, lon);
      return convertTimeStampToTimeAtLatLon(timeStamp, lat, lon);
    } catch(RuntimeException ex) {
      return ex.getMessage();
    }
  }
}

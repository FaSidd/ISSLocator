package locator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;

class ISSLocatorTest {
  private ISSLocator locator;
  private ISSWebService mockWebService;
  private final double HOUSTON_LAT = 29.7604, HOUSTON_LON = -95.3698;
  private final double INVALID_LAT = Double.POSITIVE_INFINITY, INVALID_LON =
    Double.POSITIVE_INFINITY;

  @BeforeEach
  void initializeForEachTest(){
    mockWebService = Mockito.mock(ISSWebService.class);
    locator = new ISSLocator(mockWebService);
  }

  @Test
  void canary() {
    assert(true);
  }

  @Test
  void testConvertTimeStampToUTCBehaviors() {
    assertAll(
      () -> assertEquals(
        "January, 1, 1970, 00:00:01 UTC",
        locator.convertTimeStampToUTC(1)),
      () -> assertEquals(
        "January, 1, 1970, 00:00:02 UTC",
        locator.convertTimeStampToUTC(2)),
      () -> assertEquals(
        "January, 1, 1970, 00:01:00 UTC",
        locator.convertTimeStampToUTC(60)),
      () -> assertEquals(
        "September, 22, 2019, 05:39:09 UTC",
        locator.convertTimeStampToUTC(1569130749))
    );
  }

  @Test
  void testConvertTimeStampToTimeAtLatLonHoustonBehaviors() {
    assertAll(
      () -> assertEquals(
        "December, 31, 1969, 18:00:01 CST",
        locator.convertTimeStampToTimeAtLatLon(1, 29.7604, -95.3698)),
      () -> assertEquals(
        "December, 31, 1969, 18:00:02 CST",
        locator.convertTimeStampToTimeAtLatLon(2, 29.7604, -95.3698)),
      () -> assertEquals(
        "December, 31, 1969, 18:01:00 CST",
        locator.convertTimeStampToTimeAtLatLon(60, 29.7604, -95.3698)),
      () -> assertEquals(
        "September, 22, 2019, 00:39:09 CDT",
        locator.convertTimeStampToTimeAtLatLon(1569130749, 29.7604, -95.3698))
    );
  }

  @Test
  void testConvertTimeStampToTimeAtLatLonNYC() {
    assertAll(
      () -> assertEquals(
        "December, 31, 1969, 19:00:01 EST",
        locator.convertTimeStampToTimeAtLatLon(1, 40.7128, -74.0060)),
      () -> assertEquals(
        "December, 31, 1969, 19:00:02 EST",
        locator.convertTimeStampToTimeAtLatLon(2, 40.7128, -74.0060)),
      () -> assertEquals(
        "December, 31, 1969, 19:01:00 EST",
        locator.convertTimeStampToTimeAtLatLon(60, 40.7128, -74.0060)),
      () -> assertEquals(
        "September, 22, 2019, 01:39:09 EDT",
        locator.convertTimeStampToTimeAtLatLon(1569130749, 40.7128, -74.0060))
    );
  }

  @Test
  void testConvertTimeStampToTimeAtLatLonSingaporeBehaviors() {
    assertAll(
      () -> assertEquals(
        "January, 1, 1970, 07:30:01 SGT",
        locator.convertTimeStampToTimeAtLatLon(1, 1.3521, 103.8198)),
      () -> assertEquals(
        "January, 1, 1970, 07:30:02 SGT",
        locator.convertTimeStampToTimeAtLatLon(2, 1.3521, 103.8198)),
      () -> assertEquals(
        "January, 1, 1970, 07:31:00 SGT",
        locator.convertTimeStampToTimeAtLatLon(60, 1.3521, 103.8198)),
      () -> assertEquals(
        "September, 22, 2019, 13:39:09 SGT",
        locator.convertTimeStampToTimeAtLatLon(1569130749, 1.3521, 103.8198))
    );
  }

  @Test
  void computeTimeOfFlyOverPassesLatAndLongToFetchIssFlyOverData() {
    when(mockWebService.fetchISSFlyOverData(HOUSTON_LAT,
     HOUSTON_LON)).thenReturn(2L);
    
    locator.computeTimeOfFlyOver(HOUSTON_LAT, HOUSTON_LON);
    
    verify(mockWebService).fetchISSFlyOverData(HOUSTON_LAT, HOUSTON_LON);
  }

  @Test
  void computeTimeOfFlyOverReturnsTimeBasedOnTimeStampReturnedByFetchISSFlyOverData() {
    when(mockWebService.fetchISSFlyOverData(HOUSTON_LAT, 
      HOUSTON_LON)).thenReturn(2L);
      
    assertEquals(
        "December, 31, 1969, 18:00:02 CST",
        locator.computeTimeOfFlyOver(HOUSTON_LAT, HOUSTON_LON)
    );
  }

  @Test
  void testComputeTimeOfFlyOverExceptionHandlingBehaviors() {
    when(mockWebService.fetchISSFlyOverData(INVALID_LAT, INVALID_LON))
            .thenThrow(new RuntimeException("Lat value out of Range"));

    assertEquals(
            "Lat value out of Range",
            locator.computeTimeOfFlyOver(INVALID_LAT, INVALID_LON));
  }

  @Test
  void testComputeTimeOfFlyOverNetworkErrors() {
    when(mockWebService.fetchISSFlyOverData(INVALID_LAT, INVALID_LON))
            .thenThrow(new RuntimeException("Network Error"));

    assertEquals(
            "Network Error",
            locator.computeTimeOfFlyOver(INVALID_LAT, INVALID_LON));
  }
}

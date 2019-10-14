package locator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OpenNotifyWebServiceTest {

    final String SAMPLE_JSON_PAYLOAD = "{\n" +
            "  \"message\": \"success\", \n" +
            "  \"request\": {\n" +
            "    \"altitude\": 100, \n" +
            "    \"datetime\": 1570055388, \n" +
            "    \"latitude\": 29.72167, \n" +
            "    \"longitude\": -95.343631, \n" +
            "    \"passes\": 1\n" +
            "  }, \n" +
            "  \"response\": [\n" +
            "    {\n" +
            "      \"duration\": 276, \n" +
            "      \"risetime\": 1570066535\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    OpenNotifyWebService service;

    @BeforeEach
    void beforeEach() {
        service = new OpenNotifyWebService();
    }

    @Test
    void parseJSONPayloadBehaviors() {
        final String sampleJSONFormat = "{\n" +
                "  \"message\": \"success\", \n" +
                "  \"request\": {\n" +
                "    \"altitude\": 100, \n" +
                "    \"datetime\": 1570050768, \n" +
                "    \"latitude\": 29.72167, \n" +
                "    \"longitude\": -95.343631, \n" +
                "    \"passes\": 1\n" +
                "  }, \n" +
                "  \"response\": [\n" +
                "    {\n" +
                "      \"duration\": 310, \n" +
                "      \"risetime\": %d\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        assertAll(
                () -> assertEquals("1570054661", service.parseJSONPayload(String.format(sampleJSONFormat, 1570054661))),
                () -> assertEquals("1324123413", service.parseJSONPayload(String.format(sampleJSONFormat, 1324123413))),
                () -> assertEquals("4", service.parseJSONPayload(String.format(sampleJSONFormat, 4)))
        );                                                   
    }

    @Test
    void parseJSONPayloadBadLatAndLonBehaviors() {
        final String badJSONFormat = "{\n" +
                "  \"message\": \"failure\", \n" +
                "  \"reason\": \"%s\"\n" +
                "}";

        final String badLatJSON = String.format(badJSONFormat, "Latitude must be number between -90.0 and 90.0");
        final String badLonJSON = String.format(badJSONFormat, "Longitude must be number between -180.0 and 180.0");

        RuntimeException badLatEx = assertThrows(RuntimeException.class, () -> service.parseJSONPayload(badLatJSON));
        RuntimeException badLonEx = assertThrows(RuntimeException.class, () -> service.parseJSONPayload(badLonJSON));

        assertAll(
                () -> assertEquals("Latitude must be number between -90.0 and 90.0", badLatEx.getMessage()),
                () -> assertEquals("Longitude must be number between -180.0 and 180.0", badLonEx.getMessage())
        );
    }
         
    @Test
    void fetchISSFlyOverDataCallsServiceAndPassesResponseToParseData() throws Exception {
        OpenNotifyWebService webService = spy(OpenNotifyWebService.class);
        doReturn(SAMPLE_JSON_PAYLOAD).when(webService).apiGetRequest(1.0,1.0);
        webService.fetchISSFlyOverData(1.0, 1.0);
        verify(webService).apiGetRequest(1.0, 1.0);
        verify(webService).parseJSONPayload(SAMPLE_JSON_PAYLOAD);
    }

    @Test
    void fetchISSFlyOverDataCallsServiceAndReturnsTimeStampReturnedByParseData() throws Exception {
        OpenNotifyWebService webService = spy(OpenNotifyWebService.class);
        doReturn(SAMPLE_JSON_PAYLOAD).when(webService).apiGetRequest(1.0, 1.0);
        doReturn("45").when(webService).parseJSONPayload(SAMPLE_JSON_PAYLOAD);
        assertEquals(45L, webService.fetchISSFlyOverData(1.0, 1.0));
    }

    @Test
    void fetchISSFlyOverDataReturnsErrorReturnedByParseData() throws Exception {
        OpenNotifyWebService webService = spy(OpenNotifyWebService.class);
        String expectedErrorMessage = "Invalid Lat / Long";

        doReturn(SAMPLE_JSON_PAYLOAD).when(webService).apiGetRequest(0d,0d);
        doThrow(new RuntimeException(expectedErrorMessage)).when(webService).parseJSONPayload(SAMPLE_JSON_PAYLOAD);

        RuntimeException re = assertThrows(RuntimeException.class, () -> webService.fetchISSFlyOverData(0d,0d));

        assertEquals(expectedErrorMessage, re.getMessage());
    }

    @Test
    void fetchISSFlyOverDateBubblesUpNetworkErrorFromAPICall() throws Exception {
        OpenNotifyWebService webService = spy(OpenNotifyWebService.class);
        final String expectedErrorMessage = "Network IO Exception or Something";

        doThrow(new RuntimeException(expectedErrorMessage)).when(webService).apiGetRequest(1.0, 1.0);
        RuntimeException re = assertThrows(RuntimeException.class, () -> webService.fetchISSFlyOverData(1.0, 1.0));

        assertEquals(expectedErrorMessage, re.getMessage());
    }

    @Test
    void fetchISSFlyOverDataReturnsSomeTimeStampForSomeLatAndLon() {
        assertTrue(
          service.fetchISSFlyOverData(10.0, 10.0) > 0L
        );
    }

}
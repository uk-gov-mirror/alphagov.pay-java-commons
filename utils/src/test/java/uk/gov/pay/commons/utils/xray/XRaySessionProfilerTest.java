package uk.gov.pay.commons.utils.xray;

import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class XRaySessionProfilerTest {
    @Captor
    ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    private DatabaseQuery mockedQuery = mock(DatabaseQuery.class);
    private Record mockedRecord = mock(AbstractRecord.class);
    private AbstractSession mockedSession = mock(AbstractSession.class);
    private AWSXRayRecorder mockedRecorder = mock(AWSXRayRecorder.class);
    private Segment mockedSegment = mock(Segment.class);
    private Subsegment mockedSubSegment = mock(Subsegment.class);
    private DatabaseLogin loginDetails = new DatabaseLogin();
    private SessionProfiler xrayProfiler;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        xrayProfiler = new XRaySessionProfiler(mockedRecorder);
        when(mockedRecorder.getCurrentSegmentOptional()).thenReturn(Optional.of(mockedSegment));
        when(mockedRecorder.beginSubsegment(anyString())).thenReturn(mockedSubSegment);
        loginDetails.setURL("jdbc:postgresql://localhost:32788/connector_tests");
        loginDetails.setUserName("test username");
        when(mockedSession.getDatasourceLogin()).thenReturn(loginDetails);
        when(mockedQuery.getMonitorName()).thenReturn("Test Monitor");
        when(mockedQuery.getSQLString()).thenReturn("SELECT 1");
        when(mockedQuery.isCallQuery()).thenReturn(false);
    }

    @Test
    public void shouldPopulateFieldsForSegment() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        xrayProfiler.profileExecutionOfQuery(mockedQuery, mockedRecord, mockedSession);

        verify(mockedRecorder).beginSubsegment(argumentCaptor.capture());
        verify(mockedSubSegment).putAllSql(mapArgumentCaptor.capture());

        String actualURI = argumentCaptor.getValue();

        assertEquals("connector_tests@localhost", actualURI);

        Map<String, Object> actualSQLMap = mapArgumentCaptor.getValue();
        assertEquals("statement", actualSQLMap.get("preparation"));
        assertEquals("test username", actualSQLMap.get("user"));
        assertEquals("jdbc:postgresql://localhost:32788/connector_tests", actualSQLMap.get("url"));
        assertEquals("SELECT 1", actualSQLMap.get("sanitized_query"));

        assertEquals(SessionProfiler.ALL, xrayProfiler.getProfileWeight());
    }
    
    @Test
    public void shouldNotPopulateFieldsWhenNullDatabaseLogin() {
        when(mockedSession.getDatasourceLogin()).thenReturn(null);

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        xrayProfiler.profileExecutionOfQuery(mockedQuery, mockedRecord, mockedSession);

        verify(mockedRecorder).beginSubsegment(argumentCaptor.capture());
        verify(mockedSubSegment).putAllSql(mapArgumentCaptor.capture());

        String actualURI = argumentCaptor.getValue();

        assertEquals("database", actualURI);

        Map<String, Object> actualSQLMap = mapArgumentCaptor.getValue();
        assertEquals("statement", actualSQLMap.get("preparation"));

        assertNull(actualSQLMap.get("user"));
        assertNull(actualSQLMap.get("url"));
        assertEquals("SELECT 1", actualSQLMap.get("sanitized_query"));

        assertEquals(SessionProfiler.ALL, xrayProfiler.getProfileWeight());
    }
}

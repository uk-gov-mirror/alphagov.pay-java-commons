package uk.gov.pay.commons.utils.xray;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.Namespace;
import com.amazonaws.xray.entities.Subsegment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class XRaySessionProfiler implements SessionProfiler {
    private int profileWeight = SessionProfiler.ALL;
    private static final Logger logger = LoggerFactory.getLogger(XRaySessionProfiler.class);
    private final AWSXRayRecorder recorder = AWSXRay.getGlobalRecorder();

    @Override
    public void endOperationProfile(String operationName) {
    }

    @Override
    public void endOperationProfile(String operationName, DatabaseQuery databaseQuery, int weight) {
    }

    @Override
    public Object profileExecutionOfQuery(DatabaseQuery databaseQuery, Record record, AbstractSession abstractSession) {

        HashMap<String, Object> databaseMetadata = new HashMap<>();
        String hostname = getDatabaseHostnameAndMetadata(databaseQuery, abstractSession, databaseMetadata);

        if (recorder.getCurrentSegmentOptional().isPresent()) {
            Subsegment subsegment = recorder.beginSubsegment(hostname);
            subsegment.putMetadata("monitor_name", databaseQuery.getMonitorName());
            subsegment.putMetadata("calling_class", databaseQuery.getClass().getSimpleName());
            subsegment.setNamespace(Namespace.REMOTE.toString());
            subsegment.putAllSql(databaseMetadata);

            try {
                return abstractSession.internalExecuteQuery(databaseQuery, (AbstractRecord) record);
            } finally {
                subsegment.end();
            }
        } else {
            logger.info("No segment found, executing query without recording subsegment data.");
            return abstractSession.internalExecuteQuery(databaseQuery, (AbstractRecord) record);
        }
    }

    private String getDatabaseHostnameAndMetadata(DatabaseQuery databaseQuery, AbstractSession abstractSession, HashMap<String, Object> databaseMetadata) {
        String hostname = "database";

        Accessor accessor = abstractSession.getAccessor();
        if (accessor == null) {
            return hostname;
        }
        Connection connection = accessor.getConnection();
        if (connection == null) {
            return hostname;
        }
        updateMetadata(databaseQuery, databaseMetadata, connection);

        return getDatabaseHostName(connection);
    }

    private void updateMetadata(DatabaseQuery databaseQuery, HashMap<String, Object> databaseMetadata, Connection connection) {
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            databaseMetadata.put("url", metadata.getURL());
            databaseMetadata.put("user", metadata.getUserName());
            databaseMetadata.put("driver_version", metadata.getDriverVersion());
            databaseMetadata.put("database_type", metadata.getDatabaseProductName());
            databaseMetadata.put("database_version", metadata.getDatabaseProductVersion());
            databaseMetadata.put("preparation", databaseQuery.isCallQuery() ? "call" : "statement");
            databaseMetadata.put("sanitized_query", StringUtils.isEmpty(databaseQuery.getSQLString()) ? "" : databaseQuery.getSQLString());
        } catch (SQLException exception) {
            logger.warn("Error getting database connection details.");
        }
    }

    private String getDatabaseHostName(Connection connection) {
        String hostname = "database";
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            hostname = new URI((new URI(metadata.getURL())).getSchemeSpecificPart()).getHost();
            return connection.getCatalog() + "@" + hostname;
        } catch (URISyntaxException exception) {
            logger.warn("Error parsing database host name.");
        } catch (SQLException exception) {
            logger.warn("Error getting database connection details.");
        }
        return hostname;
    }

    @Override
    public void setSession(Session session) {
    }

    @Override
    public void startOperationProfile(String operationName) {
    }

    @Override
    public void startOperationProfile(String operationName, DatabaseQuery databaseQuery, int weight) {
    }

    @Override
    public void update(String operationName, Object value) {
    }

    @Override
    public void occurred(String operationName, AbstractSession abstractSession) {
    }

    @Override
    public void occurred(String operationName, DatabaseQuery databaseQuery, AbstractSession abstractSession) {
    }

    @Override
    public void setProfileWeight(int profileWeight) {
        this.profileWeight = profileWeight;
    }

    @Override
    public int getProfileWeight() {
        return this.profileWeight;
    }

    @Override
    public void initialize() {
    }
}

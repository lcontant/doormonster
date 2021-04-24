package API.Util.Repositories;

import static API.databases.tables.Session.SESSION;

import API.Model.SessionDto;
import API.Util.SQLConnector.ConnectionManager;
import API.Util.SQLConnector.ConnectionPoolManager;
import API.databases.tables.Session;
import API.databases.tables.records.SessionRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
@Component
public class SessionRepository {

    ConnectionManager connectionManager;
    ConnectionPoolManager connectionPoolManager;
    DSLContext create;

    public SessionRepository(ConnectionManager connectionManager, ConnectionPoolManager connectionPoolManager) {
        this.connectionManager = connectionManager;
        this.connectionPoolManager = connectionPoolManager;
        this.create = DSL.using(this.connectionPoolManager.getConnection().connection, SQLDialect.MYSQL);
    }

    public SessionDto getSession(String sessionId) {
        Result<SessionRecord> result = this.create.selectFrom(SESSION).where(SESSION.SESSIONID.eq(sessionId)).fetch();
        SessionDto session = null;
        if (!result.isEmpty()) {
           session = new SessionDto(result.get(0));
        }
        return session;
    }

    public synchronized SessionDto createSession(String sessionId, int userId) {
        this.create.insertInto(SESSION, SESSION.SESSIONID, SESSION.USERID).values(sessionId, Long.valueOf(userId)).execute();
        return getSession(sessionId);
    }

    public synchronized boolean deleteSession(String sessionId) throws SQLException {
        String deleteRequest = "Delete from session where sessionId = '" + sessionId +"'";
        int result = this.connectionManager.update(deleteRequest);
        return result > 0;
    }

}

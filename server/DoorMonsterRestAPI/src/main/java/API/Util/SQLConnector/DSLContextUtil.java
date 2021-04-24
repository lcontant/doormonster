package API.Util.SQLConnector;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class DSLContextUtil {


  public static DSLContext getContext(Connection connection) {
       return DSL.using(connection, SQLDialect.MYSQL);
    }

}

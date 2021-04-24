package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Role
{
    public static final String TABLE_NAME = "ROLE";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String NAME_COLUMN_NAME = "NAME";
    public static final String RANK_COLUMN_NAME = "RANK";
    public String id;
    public String name;
    public int ranking;

    public Role(String id, String name,int ranking) {
        this.id = id;
        this.name = name;
        this.ranking = ranking;
    }

    public Role(ResultSet rs) throws SQLException {
        this(rs.getString(ID_COLUMN_NAME), rs.getString(NAME_COLUMN_NAME),rs.getInt(RANK_COLUMN_NAME));
    }
}

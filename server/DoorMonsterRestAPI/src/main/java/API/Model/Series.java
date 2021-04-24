package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.persistence.Column;
import javax.persistence.Id;

public class Series {
    public static final String TITLE_COLUMN_NAME = "TITLE";
    public static final String TEXT_ID_COLUMN_NAME = "TEXTID";
    public static final String DESCRIPTION_COLUMN_NAME = "DESCRIPTION";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String CREATOR_ID_COLUMN_NAME = "CREATOR_ID";
    public static final String TABLE_NAME = "SERIES";
    @Id
    public int id;
    @Column(name= TITLE_COLUMN_NAME)
    public String title;
    /**
     * Used for aws dynamic path storage
     */
    @Column(name=TEXT_ID_COLUMN_NAME)
    public String textId;
    @Column(name=CREATOR_ID_COLUMN_NAME)
    public int creatorId;
    @Column(name = DESCRIPTION_COLUMN_NAME)
    public String description;

    public Series() {

    }
    public Series(int id,int creatorId, String title, String TEXTID, String description) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.textId = TEXTID;
        this.description = description;
    }

    public Series(ResultSet rs ) throws SQLException {
        this(rs, "");
    }

    public Series(ResultSet rs, String prefix) throws SQLException {
        this(rs.getInt(prefix + ID_COLUMN_NAME)
                , rs.getInt(prefix + CREATOR_ID_COLUMN_NAME)
                ,rs.getString(prefix + TITLE_COLUMN_NAME)
                ,rs.getString(prefix + TEXT_ID_COLUMN_NAME)
                ,rs.getString(prefix + DESCRIPTION_COLUMN_NAME)
                );
    }
}

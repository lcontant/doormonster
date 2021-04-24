package API.Model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tag {

    public static final String TABLE_NAME = "TAGS";
    public static final String VIDEO_ID_COLUMN_NAME = "VIDEO_ID";
    public static final String VALUE_COLUMN_NAME = "VALUE";
    public static final String ID_COLUMN_NAME = "ID";
    public static final String WEIGHT_COLUMN_NAME = "WEIGHT";

    public int id;
    public String value;
    public int videoId;
    public int weight;

    public Tag(int id, String value, int videoId, int weight) {
        this.id = id;
        this.value = value;
        this.videoId = videoId;
        this.weight = weight;
    }

    public Tag(ResultSet rs) throws SQLException {
       this(rs.getInt(ID_COLUMN_NAME)
               , rs.getString(VALUE_COLUMN_NAME)
               , rs.getInt(VIDEO_ID_COLUMN_NAME)
                , rs.getInt(WEIGHT_COLUMN_NAME));
    }

    @Override
    public boolean equals(Object obj) {
       if (!obj.getClass().equals(this.getClass()))  {
          return  super.equals(obj);
       } else {
           return this.value.equals(((Tag)obj).value) && this.videoId == ((Tag) obj).videoId;
       }
    }
}

package API.Util.Repositories;

import API.Model.Vote;
import API.Util.SQLConnector.ConnectionManager;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class VoteRepository {


    private static String GET_ALL_BASE_REQUEST = "Select * from votes";

    ConnectionManager connectionManager;

    public VoteRepository(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public synchronized boolean insertVote(Vote vote) {
        boolean insertSuccesful = false;
            String request = String.format("Insert into votes (userid, commentid, isUpVote) VALUES (%d,%d,%d)",
                    vote.userId,
                    vote.commentId,
                    vote.isUpVote ? 1: 0);
            try {
                int rowsAffected = this.connectionManager.update(request);
                insertSuccesful = rowsAffected == 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return insertSuccesful;
    }

    public synchronized Vote getByUserAndComment(Vote vote) {
        String request = String.format("%s where commentid= %d and userid= %d", GET_ALL_BASE_REQUEST, vote.commentId, vote.userId);
        Vote returnVote = null;
        try {
            ResultSet rs = this.connectionManager.query(request);
            if (rs.next()) {
                returnVote = new Vote(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnVote;
    }

    public synchronized boolean deleteVotesOnComment(int commentId) {
        String request = String.format("Delete from votes where commentid = %d", commentId);
        boolean deleteSuccesfull = false;
        try {
            int affectedRows = this.connectionManager.update(request);
            deleteSuccesfull = affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deleteSuccesfull;
    }

    public synchronized void updateVote(Vote vote) {
        String request = String.format("Update votes set isUpVote = %d where commentid = %d AND userid = %d", vote.isUpVote ? 1 : 0, vote.commentId, vote.userId);
        try {
            this.connectionManager.update(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteVote(Vote vote) {
        String request = String.format("Delete from votes where commentid=%d AND userid=%d",vote.commentId, vote.userId, vote.isUpVote ? 1 : 0);
        try {
            this.connectionManager.update(request);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Vote> getVotesForUser(int userId) throws SQLException {
        String request = String.format("Select * from votes where userid=%d", userId);
        List<Vote> votes = new ArrayList<>();
       ResultSet rs = this.connectionManager.query(request);
        while (rs.next()) {
            votes.add(new Vote(rs));
        }
        return votes;
    }


    public synchronized boolean hasAlreadyVoted(Vote vote) {
        String request = String.format("%s where commentid = %d AND userid = %d",GET_ALL_BASE_REQUEST,vote.commentId, vote.userId);
        boolean hasVoted = false;
        try {
            ResultSet rs = this.connectionManager.query(request);
            if (rs.next()){
                hasVoted = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return hasVoted;
    }
}

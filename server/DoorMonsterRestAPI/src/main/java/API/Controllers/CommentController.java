package API.Controllers;

import API.Model.Comment;
import API.Model.UserDto;
import API.Model.UserPageComment;
import API.Model.Vote;
import API.Util.JSONMapper;
import API.Util.Repositories.CommentRepository;
import API.Util.Repositories.UserRepository;
import API.Util.Repositories.VoteRepository;
import API.databases.tables.Comments;
import API.databases.tables.records.CommentsRecord;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("Comments")
public class CommentController {

    VoteRepository voteRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;

    public CommentController(VoteRepository voteRepository
            , CommentRepository commentRepository
            , UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getComment(@PathVariable("id") int id) {
        Comment comment = null;
        try {
            comment = this.commentRepository.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String repsonseBody = JSONMapper.getInstance().JSONStringify(comment);
        return new ResponseEntity<>(repsonseBody, HttpStatus.OK);
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<String> getCommentForMedia(@PathVariable("id") String mediaId) {
        List<Comment> comments = null;
        try {
            comments = this.commentRepository.getCommentsForMedia(mediaId);
            comments.sort((Comment o1,Comment o2) -> o2.createdOn.compareTo(o1.createdOn));
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        String responseBody = JSONMapper.getInstance().JSONStringify(comments);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/replies/{id}")
    public ResponseEntity<String> getRepliesForParentComment(@PathVariable("id") int commentId) {
        List<Comment> replies = null;
        try {
            replies = this.commentRepository.getRepliesForComment(commentId);
            replies.sort((Comment o1,Comment o2) -> o2.createdOn.compareTo(o1.createdOn));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String responseBody = JSONMapper.getInstance().JSONStringify(replies);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/create")
    @Caching(evict = {
            @CacheEvict(value = "episodes", allEntries = true),
            @CacheEvict(value = "Allepisodes", allEntries = true)
    })
    public ResponseEntity<String> insertComment(@RequestBody Comment comment, @RequestHeader("SessionId") String sessionId) {
        try {
            if (this.userRepository.isActivated(sessionId)) {
                if (!this.commentRepository.isDuplicateComment(comment)) {
                    boolean insertSuccesfull = this.commentRepository.insertComment(comment);
                    if (insertSuccesfull) {
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("User has already commented that on this video"), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("User has to be activated"), HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/delete/{id}")
    @Caching(evict = {
            @CacheEvict(value = "episodes", allEntries = true),
            @CacheEvict(value = "Allepisodes", allEntries = true)
    })
    public ResponseEntity<String> deleteComment(@PathVariable("id") int commentId, @RequestHeader("SessionId") String sessionId) {
        try {
            if (this.commentRepository.canEditComment(commentId, sessionId)) {
                boolean deleteSuccessful = false;
                try {
                    deleteSuccessful = this.commentRepository.deleteComment(commentId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (deleteSuccessful) {
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("This user can't edit this comment"), HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateComment(@RequestBody String newText, @PathVariable("id") int commentId, @RequestHeader("SessionId") String sessionId) {
        try {
            if (this.commentRepository.canEditComment(commentId, sessionId)) {
                Comment newComment = this.commentRepository.updateComment(commentId, newText);
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify(newComment), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("This user can't edit this comment"), HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(JSONMapper.getInstance().JSONStringify("This user can't edit this comment"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("vote")
    public ResponseEntity<String> voteOnComment(@RequestBody Vote vote, @RequestHeader("SessionId") String sessionId) {
        //TODO: Do a refactor when less hungry
        boolean operationSuccessFull = false;
        ResponseEntity<String> response = null;
        Vote oldVote = this.voteRepository.getByUserAndComment(vote);
        try {
            if (oldVote != null) {

                this.voteRepository.deleteVote(oldVote);
                if (!oldVote.isUpVote) {
                    operationSuccessFull = this.commentRepository.UpvoteComment(oldVote.commentId);
                } else {
                    operationSuccessFull = this.commentRepository.downVoteComment(oldVote.commentId);
                }

            } else {

                if (vote.isUpVote) {
                    operationSuccessFull = this.commentRepository.UpvoteComment(vote.commentId);
                } else {
                    operationSuccessFull = this.commentRepository.downVoteComment(vote.commentId);
                }

                if (operationSuccessFull) {
                    if (this.voteRepository.insertVote(vote)) {
                        response = new ResponseEntity<String>(JSONMapper.getInstance().JSONStringify("Vote registered")
                                , HttpStatus.OK);
                    } else {
                        response = new ResponseEntity<String>(JSONMapper.getInstance().JSONStringify("An error happened while registering your vote")
                                , HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    response = new ResponseEntity<String>(JSONMapper.getInstance().JSONStringify("Couldn't vote")
                            , HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return response;
    }

    @GetMapping("/userVote")
    public ResponseEntity<String> getAllVotesForUser(@RequestHeader("SessionId") String sessionID) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get upvotes");
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            List<Vote> votes = new ArrayList<>();
            UserDto user = this.userRepository.getBySessionId(sessionID);
            if (user != null) {
                votes = this.voteRepository.getVotesForUser(user.userId);
                response = JSONMapper.getInstance().JSONStringify(votes);
                responseStatus = HttpStatus.OK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, responseStatus);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<String> getAllCommentsForUser(@PathVariable("userId") int userId) {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get the comments");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            List<UserPageComment> comments = this.commentRepository.getCommentsForUser(userId);
            comments.sort((Comment o1,Comment o2) -> o2.createdOn.compareTo(o1.createdOn));
            response = JSONMapper.getInstance().JSONStringify(comments);
            status = HttpStatus.OK;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/top")
    public ResponseEntity<String> getTopCommentsOfTheWeek() {
        String response = JSONMapper.getInstance().JSONStringify("Couldn't get the comments");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            List<Comment> topComments = this.commentRepository.getTopCommentsOfTheWeek();
            response = JSONMapper.getInstance().JSONStringify(topComments);
            status = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, status);
    }
}

package API.Controllers;

import API.Model.Friend;
import API.Util.JSONMapper;
import API.Util.Repositories.FriendRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/friends")
public class FriendController {

    FriendRepository friendRepository;

    public FriendController(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @RequestMapping(path="",method = GET)
    public String getAll() throws SQLException {
        List<Friend> friends = this.friendRepository.getList();
        return JSONMapper.getInstance().JSONStringify(friends);
    }

    @RequestMapping(path="/random/{n}", method = GET)
    public String getNRandom(@PathVariable(value = "n") String n) throws SQLException {
        List<Friend> friends = this.friendRepository.getNRandom(Integer.parseInt(n));
        return JSONMapper.getInstance().JSONStringify(friends);
    }
}

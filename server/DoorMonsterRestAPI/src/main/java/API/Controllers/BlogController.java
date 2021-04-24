package API.Controllers;


import API.Model.Blog;
import API.Util.JSONMapper;
import API.Util.Repositories.BlogRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    BlogRepository blogRepository;

    public BlogController(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @RequestMapping(path = "",method=GET)
    public String getBlogs()  {
        List<Blog> blogs = null;
        try {
            blogs = this.blogRepository.GetList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return JSONMapper.getInstance().JSONStringify(blogs);
    }

    @RequestMapping(path= "/{id}",method = GET)
    public String getBlogPost(@PathVariable String id) {
        Blog blog = null;
        try {
            blog = this.blogRepository.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return JSONMapper.getInstance().JSONStringify(blog);
    }
}

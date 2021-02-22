import com.jeestudio.datasource.DataSourceApplication;
import com.jeestudio.datasource.entity.user.User;
import com.jeestudio.datasource.mapper.base.userDao.UserDao;
import com.jeestudio.utils.PropertiesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: Test
 * @author: whl
 * @Date: 2019-11-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceApplication.class)
public class DbTest {

    /*@Autowired
    private UserUtil userUtil;*/
    @Autowired
    private UserDao userDao;

    @Test
    public void configValue(){
        //String pagesize = PropertiesUtil.getProperty("application.properties","page.pageSize");
        //String subsystemCode = PropertiesUtil.getProperty("application.properties","subsystemCode");
    }

    @Test
    public void dbTest(){
        //userUtil.getMenuList();
    }

    @Test
    public void getUser(){
        User user = userDao.getByLoginName(new User(null, "123"));
    }
}

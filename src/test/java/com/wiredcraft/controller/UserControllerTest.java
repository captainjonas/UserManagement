package com.wiredcraft.controller;

import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.service.UserService;
import com.wiredcraft.service.VO.PageResult;
import com.wiredcraft.service.VO.UserVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import com.wiredcraft.TestApplication;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@SpringBootTest(classes = TestApplication.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Resource
    private UserController userController;

    @BeforeEach
    void setup() throws Exception {

        Mockito.when(userService.getUsers(any())).thenAnswer(a->{
            UserQueryModel query = a.getArgument(0);
            PageResult<UserVO> result = new PageResult<UserVO>();
            if(query.getCurrent() > 0L){
                result.setTotal(1L);
            }else{
                result.setTotal(0L);
            }
            return result;
        });


        Mockito.when(userService.deleteUser(Mockito.longThat(new GreaterOrEqual<Long>(0L))))
                .thenReturn(true);

        Mockito.when(userService.createUser(any())).thenAnswer(a->{
            UserVO model = a.getArgument(0);
            if("invalid".equals(model.getName())){
                return model;
            }
            model.setId(1L);
            return model;
        });

        Mockito.when(userService.updateUser(any())).thenAnswer(a->{
            UserVO model = a.getArgument(0);
            if(model.getId() == -1L){
                return null;
            }
            return model;
        });
    }

    @Test
    void testCreateUser() {
        UserVO user = new UserVO();
        user.setName("invalid");
        user.setDob(new Date());
        ResponseEntity<UserVO> invalidResult = userController.createUser(user);
        assertEquals(HttpStatus.NO_CONTENT, invalidResult.getStatusCode());

        user.setName("admin");
        user.setDob(new Date());
        ResponseEntity<UserVO> result = userController.createUser(user);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testDeleteUser() {
        ResponseEntity<Void> notFoundResult = userController.deleteUser(-1L);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResult.getStatusCode());

        ResponseEntity<Void> result = userController.deleteUser(1L);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }



    @Test
    void testGetUsers() {
        UserQueryModel query = new UserQueryModel();

        ResponseEntity<PageResult<UserVO>> okResult = userController.getUsers(query);
        assertEquals(HttpStatus.OK, okResult.getStatusCode());

        query.setCurrent(-1);
        ResponseEntity<PageResult<UserVO>> noResult = userController.getUsers(query);
        assertEquals(HttpStatus.NO_CONTENT, noResult.getStatusCode());
    }

    @Test
    void testUpdateUser() {
        UserVO user = new UserVO();
        user.setName("invalid");
        ResponseEntity<UserVO> invalidResult = userController.updateUser(user);
        assertEquals(HttpStatus.NO_CONTENT, invalidResult.getStatusCode());

        user.setName("admin");
        ResponseEntity<UserVO> result = userController.updateUser(user);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        user.setId(-1L);
        ResponseEntity<UserVO> notFoundResult = userController.updateUser(user);
        assertEquals(HttpStatus.NO_CONTENT, notFoundResult.getStatusCode());

        user.setId(1L);
        ResponseEntity<UserVO> createResult = userController.updateUser(user);
        assertEquals(HttpStatus.OK, createResult.getStatusCode());
        assertNotNull(createResult.getBody());
    }
}

package com.wiredcraft.service.impl;

import com.wiredcraft.TestApplication;
import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.dao.repository.UserRepository;
import com.wiredcraft.model.UserInfo;
import com.wiredcraft.service.UserService;
import com.wiredcraft.service.VO.PageResult;
import com.wiredcraft.service.VO.UserVO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.annotation.Resource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doAnswer;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@SpringBootTest(classes = TestApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {


    @MockBean
    private UserRepository userRepository;


    @Resource
    private UserService userService;

    private static Map<Long, UserInfo> mockDb = new HashMap<>();

    private static Map<String, UserInfo> mockUserDbByName = new HashMap<>();

    private static Long idSeed = 4L;

    @BeforeAll
    static void setUpStaticVariables() throws Exception {

        for (Integer i = 0; i < 3; i++) {
            UserInfo user = new UserInfo();
            String name = "user" + i;
            user.setName(name);
            user.setDeleted(false);
            user.setId(i.longValue());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            mockDb.put(i.longValue(), user);
            mockUserDbByName.put(name, user);
        }
    }

    @BeforeEach
    void setup() {
        Mockito.reset(userRepository);

        Mockito.when(userRepository.getByName(Mockito.anyString())).thenAnswer(a->{
            String userName = a.getArgument(0, String.class);
            return mockUserDbByName.get(userName);
        });


        Mockito.when(userRepository.addUser(Mockito.any())).thenAnswer(a->{
            UserInfo user = a.getArgument(0);
            user.setId(idSeed++);
            user.setDeleted(false);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            mockDb.put(user.getId(), user);
            mockUserDbByName.put(user.getName(), user);
            return user;
        });

        Mockito.when(userRepository.getUsers(Mockito.any())).thenAnswer(a->{
            List<UserInfo> users = new ArrayList<UserInfo>(mockDb.values());
            Page<UserInfo> page = new Page<>(1, 10);
            page.setRecords(users);
            page.setTotal(mockDb.size());
            return page;
        });

        doAnswer(a->{
            UserInfo user = a.getArgument(0);
            mockDb.remove(user.getId());
            mockUserDbByName.remove(user.getName());
            return null;
        }).when(userRepository).deleteUser(Mockito.any());

        doAnswer(a->{
            UserInfo user = a.getArgument(0);
            mockDb.put(user.getId(), user);
            mockUserDbByName.put(user.getName(), user);
            return null;
        }).when(userRepository).updateSelective(Mockito.any());

    }

    @Test
    @Order(1)
    void testCreateUser() {
        UserVO model = new UserVO();
        model.setName("user4");
        model.setDescription("create from unit test code");
        model.setDob(new Date());
        assertNotNull(userService.createUser(model));

    }


    @Test
    @Order(2)
    void testDeleteUser() {
        userService.deleteUser(--idSeed);
        assertEquals(3, mockDb.size());
        assertEquals(3, mockUserDbByName.size());
    }


    @Test
    void testGetUsers() {
        UserQueryModel query = new UserQueryModel();
        PageResult<UserVO> users = userService.getUsers(query);
        assertNotNull(users);
        assertNotNull(users.getData());
        assertEquals(3, users.getTotal());
    }


    @Test
    void testUpdateUser() {
        UserVO model = new UserVO();
        model.setId(1L);
        model.setDescription("this is description");
        UserVO update = userService.updateUser(model);
        assertNotNull(update);
    }


}

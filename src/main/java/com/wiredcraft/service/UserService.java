package com.wiredcraft.service;

import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.service.VO.PageResult;
import com.wiredcraft.service.VO.UserVO;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
public interface UserService {

    /**
     * get user list
     *
     * @param query query param
     * @return list of user models
     */
    PageResult<UserVO> getUsers(UserQueryModel query);

    /**
     * insert user
     *
     * @param insertModel model to insert
     * @return inserted user model
     */
    UserVO createUser(UserVO insertModel);

    /**
     * upodate user
     *
     * @param updateModel the update content
     * @return updated user model
     */
    UserVO updateUser(UserVO updateModel);


    /**
     * delete user
     *
     * @param userId user id
     */
    Boolean deleteUser(Long userId);
}

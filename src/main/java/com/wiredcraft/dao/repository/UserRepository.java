package com.wiredcraft.dao.repository;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wiredcraft.dao.UserDao;
import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.model.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@Repository
public class UserRepository extends ServiceImpl<UserDao, UserInfo> {


    /**
     * get userInfo by userId
     * @param userId Long
     * @return
     */
    public UserInfo getUserById(Long userId){
        Wrapper<UserInfo> userQuery = new QueryWrapper<UserInfo>().lambda()
                .eq(UserInfo::getId, userId)
                .eq(UserInfo::getDeleted, false);
        return this.baseMapper.selectOne(userQuery);
    }

    /**
     * using query object search the userInfos (inclue pagination)
     *
     * @param query
     * @return
     */
    public Page<UserInfo> getUsers(UserQueryModel query) {
        Wrapper<UserInfo> queryWrapper = new QueryWrapper<UserInfo>().lambda()
                .eq(UserInfo::getDeleted, false);
        Page<UserInfo> page = new Page<>(query.getCurrent(), query.getSize());
        page.setSearchCount(true);
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    /**
     * add new user
     * @param user
     * @return
     */
    public UserInfo addUser(UserInfo user) {
        user.setCreatedAt(new Date());
        this.save(user);
        return this.getById(user.getId());
    }

    /**
     * update user by parameters
     *
     * @param user
     */
    public void updateSelective(UserInfo user) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<UserInfo>().lambda();
        updateWrapper.eq(UserInfo::getId, user.getId())
                .set(StringUtils.isNotBlank(user.getAddress()), UserInfo::getAddress, user.getAddress())
                .set(user.getDob() != null, UserInfo::getDob, user.getDob())
                .set(StringUtils.isNotBlank(user.getDescription()), UserInfo::getDescription, user.getDescription())
                .set(UserInfo::getUpdatedAt, new Date());
        this.baseMapper.update(null, updateWrapper);
    }

    /**
     * delete user, set logic delete flag to true
     *
     * @param userId
     */
    public void deleteUser(Long userId) {
        LambdaUpdateWrapper<UserInfo> updateWrapper=new UpdateWrapper<UserInfo>().lambda();
        updateWrapper.eq(UserInfo::getId, userId)
                .set(UserInfo::getDeleted, true)
                .set(UserInfo::getUpdatedAt, new Date());
        this.baseMapper.update(null, updateWrapper);
    }

    /**
     * get user by name
     * @param userName
     * @return
     */
    public UserInfo getByName(String userName) {
        Wrapper<UserInfo> query = new QueryWrapper<UserInfo>().lambda()
                .eq(UserInfo::getName, userName)
                .eq(UserInfo::getDeleted, false);
        return this.baseMapper.selectOne(query);
    }


}
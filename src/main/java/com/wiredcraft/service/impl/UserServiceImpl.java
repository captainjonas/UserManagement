package com.wiredcraft.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiredcraft.common.BizException;
import com.wiredcraft.common.ErrCodeEnum;
import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.dao.repository.UserRepository;
import com.wiredcraft.model.UserInfo;
import com.wiredcraft.service.UserService;
import com.wiredcraft.service.VO.PageResult;
import com.wiredcraft.service.VO.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eric Yao
 * @date 2022-10-30
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Value("${spring.redis.userKey}")
    private String userKey;

    @Override
    public PageResult<UserVO> getUsers(UserQueryModel query) {
        Page<UserInfo> userPage = userRepository.getUsers(query);
        List<UserVO> userModels = userPage.convert(c -> {
            UserVO model = new UserVO();
            BeanUtils.copyProperties(c, model);
            return model;
        }).getRecords();

        PageResult<UserVO> result = new PageResult<UserVO>();
        result.setData(userModels);
        result.setCurrent(userPage.getCurrent());
        result.setSize(userPage.getSize());
        result.setTotal(userPage.getTotal());

        return result;
    }

    @Override
    public UserVO createUser(UserVO insertModel) {
        UserInfo existedUser = userRepository.getByName(insertModel.getName());
        if(null !=existedUser){
            throw new BizException(ErrCodeEnum.ILLEGAL_ARGUMENTS.getErrorCode(),
                    "This is username has been registered, please change another username");
        }
        UserInfo newUser=new UserInfo();
        newUser.setName(insertModel.getName());
        newUser.setAddress(insertModel.getAddress());
        newUser.setDeleted(false);
        newUser.setDescription(insertModel.getDescription());
        newUser.setDob(insertModel.getDob());
        userRepository.addUser(newUser);
        return insertModel;
    }

    @Override
    public UserVO updateUser(UserVO updateModel) {
        Long userId = updateModel.getId();
        if(null == userId){
            throw  new BizException(ErrCodeEnum.ILLEGAL_ARGUMENTS.getErrorCode(),ErrCodeEnum.ILLEGAL_ARGUMENTS.getErrorMsg());
        }
        Object userInfoPO=redisTemplate.opsForHash().get(userKey,String.valueOf(userId));
        if(null == userInfoPO){
            throw new BizException(ErrCodeEnum.USER_NOT_FOUND.getErrorCode(), ErrCodeEnum.USER_NOT_FOUND.getErrorMsg());
        }
        String key="updateUser"+userId;
        RLock lock=redissonClient.getLock(key);
        try{
            if(lock.tryLock(30, TimeUnit.SECONDS)){
                UserInfo existsUserPO= (UserInfo)userInfoPO;
                existsUserPO.setAddress(updateModel.getAddress());
                existsUserPO.setDescription(updateModel.getDescription());
                existsUserPO.setDob(updateModel.getDob());
                existsUserPO.setName(updateModel.getName());
                userRepository.updateSelective(existsUserPO);
            }
        }catch (Exception e){
            log.error("error occurs when update user {}, errMsg: {}", updateModel.getId(), e.getMessage());
        }finally {
            lock.unlock();
        }
        return updateModel;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        if(null == userId){
            throw  new BizException(ErrCodeEnum.ILLEGAL_ARGUMENTS.getErrorCode(),ErrCodeEnum.ILLEGAL_ARGUMENTS.getErrorMsg());
        }
        Object userInfoPO=redisTemplate.opsForHash().get(userKey,String.valueOf(userId));
        if(null == userInfoPO){
            throw new BizException(ErrCodeEnum.USER_NOT_FOUND.getErrorCode(), ErrCodeEnum.USER_NOT_FOUND.getErrorMsg());
        }
        userRepository.deleteUser(userId);
        return Boolean.TRUE;
    }
}

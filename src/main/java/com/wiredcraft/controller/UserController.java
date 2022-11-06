package com.wiredcraft.controller;

import com.wiredcraft.dao.queryModel.UserQueryModel;
import com.wiredcraft.service.UserService;
import com.wiredcraft.service.VO.PageResult;
import com.wiredcraft.service.VO.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Eric Yao
 * @date 2022-11-06
 */
@Api(value = "user service api")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    @Resource
    private UserService userService;


    @ApiOperation("query userInfo")
    @GetMapping("/list")
    public ResponseEntity<PageResult<UserVO>> getUsers(UserQueryModel query) {
        PageResult<UserVO> result = userService.getUsers(query);
        if (result.getTotal() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("create a new user")
    @PostMapping("/create")
    public ResponseEntity<UserVO> createUser(@RequestBody UserVO user) {
        UserVO result = userService.createUser(user);
        if (result.getId() != null && result.getId() > 0) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

    @ApiOperation("modify user info")
    @PutMapping("/update")
    public ResponseEntity<UserVO> updateUser(@RequestBody UserVO user) {
        UserVO result;
        HttpStatus responseStatus = null;
        if (user.getId() == null) {
            result = userService.createUser(user);
            if (result.getId() != null && result.getId() > 0) {
                responseStatus = HttpStatus.CREATED;
            }
        } else {
            result = userService.updateUser(user);
            if (result != null && result.getId() != null && result.getId() > 0) {
                responseStatus = HttpStatus.OK;
            }
        }
        if (responseStatus == null) {
            // neither create or update is failed
            responseStatus = HttpStatus.NO_CONTENT;
        }
        return ResponseEntity.status(responseStatus).body(result);
    }

    @ApiOperation("delete a user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        if (userService.deleteUser(userId)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }



}

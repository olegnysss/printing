package com.netcracker.educ.printing.controller;

import com.netcracker.educ.printing.model.entity.User;
import com.netcracker.educ.printing.model.representationModel.UserRepresent;
import com.netcracker.educ.printing.security.UserDetailsImpl;
import com.netcracker.educ.printing.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserRestController {


    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService=userService;
    }


    @GetMapping()
    public UserRepresent getCurrentUser(){
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("Get current user {}",principal.getId());
        return  userService.getCurrentUser(principal);

    }



    @GetMapping("{id}")
    public UserRepresent getUserById(@PathVariable("id") User user){
        log.debug("Get user by id= {}",user.getId());
        return userService.userToUserRepresent(user);
    }

    //Возвращает всех Исполнителей
    @GetMapping("/executors")
    public List<UserRepresent> listExecutors(){
        log.debug("Get list of executors");
        return userService.findAllExecutors();
    }

    @PutMapping("/update/{id}")
    public UserRepresent updateProfile(@RequestBody UserRepresent user){
        log.debug("User {} update his profile",user.getId());
        return userService.updateUser(user);
    }

    @GetMapping("/role")
    public Boolean checkUserRoleCustomer(){
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("Check userRole for current user {}",principal.getId());
        return userService.checkUserRole(principal.getId());
    }
    @GetMapping("/role/{id}")
    public Boolean checkUserRoleCustomerById(@PathVariable("id") UUID id){
        log.debug("Check userRole for user by id {}",id);
        return userService.checkUserRole(id);
    }




}

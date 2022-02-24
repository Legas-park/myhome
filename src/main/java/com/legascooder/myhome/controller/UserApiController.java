package com.legascooder.myhome.controller;


import com.legascooder.myhome.model.Board;
import com.legascooder.myhome.model.User;
import com.legascooder.myhome.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
class UserApiController { //RESTful api 만들기

    @Autowired
    private UserRepository repository;


    @GetMapping("/users")
    Iterable<User> all(@RequestParam(required = false) String method, @RequestParam(required = false) String text) {
        Iterable<User> users = null;
        if("query".equals(method)){
            users = repository.findByUsernameQuery(text); // method가 "query일때만 지정한 findByUsernameQuery가 호출될것임 like검색 정상적으로 작동
        } else  if("nativeQuery".equals(method)){
            users = repository.findByUsernameNativeQuery(text);
        }else{
            users = repository.findAll();
        }
        return users;
//        else if("querydsl".equals(method)){
//            QUser user = QUser.user;
//            Predicate predicate = user.username.contains(text);
//
//            users = repository.findAll(predicate);
//        } 자바 버전이슈로 사용방법만 배움
    }

    @PostMapping("/users") // Post요청을 하면 새로운 데이터가 생기도록 설정
    User newUser(@RequestBody User newUser) { //클라이언트가 요청하는 json 형태의 HTTP Body내용을 JavaObject로 변환 newUser에 내용을 변환해줌
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}") // /api/users/id = id에 해당하는 숫자를 요청하면 해당 데이터를 호출
    User one(@PathVariable Long id) { // PathVariable 클라이언트가 요청하는 url에서 각 구분자에 들어오는 값을 처리하기 위함 // 이경우는 id에 해당되는 값을 처리하기위해 사용
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) { // 수정할 내용을 newUser에 담고 id를 찾아서 정보를 수정함

        return repository.findById(id) // 우선 수정해야되는 id를 찾고, title에 수정된 내용이랑 content에 수정된 내용을 newUser오브젝트로 변환된 내용을 user에 저장
                .map(user -> {
//                    user.setTitle(newUser.getTitle());
//                    user.setContent(newUser.getContent());
//                    user.setBoards((newUser.getBoards()));
                    user.getBoards().clear();
                    user.getBoards().addAll(newUser.getBoards());
                    for(Board board : user.getBoards()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
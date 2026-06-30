package com.example.restapiuser.controller;

import com.example.restapiuser.dto.MessageResponse;
import com.example.restapiuser.dto.UserCreateRequest;
import com.example.restapiuser.dto.UserResponse;
import com.example.restapiuser.dto.UserUpdateRequest;
import com.example.restapiuser.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController      // @Controller + @ResponseBody
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // GET http://localhost:8080/api/users
    // GET http://localhost:8080/api/users?keyword=user
    // JWT 필요: Authorization: Bearer <accessToken>
    @GetMapping
    public List<UserResponse> list(
            @RequestParam(required = false) String keyword) {
        return userService.findUsers(keyword);
    }

    // GET http://localhost:8080/api/users/{userid}
    @GetMapping("/{userid}")
    public UserResponse one(@PathVariable String userid) {
        return userService.findUser(userid);
    }

    // 회원가입: 토큰 없이 가능
    // POST http://localhost:8080/api/users
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{userid}")
                .buildAndExpand(response.userid())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    // PUT http://localhost:8080/api/users/{userid}
    // JWT 필요
    @PutMapping("/{userid}")
    public UserResponse update(@PathVariable String userid,
                               @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userid, request);
    }

    // DELETE http://localhost:8080/api/users/{userid}
    // JWT 필요
    @DeleteMapping("/{userid}")
    public MessageResponse delete(@PathVariable String userid) {
        userService.deleteUser(userid);
        return new MessageResponse("삭제되었습니다: " + userid);
    }
}

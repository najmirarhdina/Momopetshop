package com.management.momopetshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

import com.management.momopetshop.model.Users;
import com.management.momopetshop.service.UsersService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // =============================
    // GET ALL USERS
    // =============================
    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Integer id) {
        return usersService.getUserById(id);
    }

    // GET USER BY USERNAME
    @GetMapping("/username/{username}")
    public Users getUserByUsername(@PathVariable String username) {
        return usersService.getUserByUsername(username);
    }

    // CREATE USER
    @PostMapping
    public Users createUser(@RequestBody Users user) {
        return usersService.createUser(user);
    }

    // =============================
    // UPDATE USER
    // =============================
    @PutMapping("/{id}")
    public Users updateUser(
            @PathVariable Integer id,
            @RequestBody Users user
    ) {
        user.setIdUser(id);
        return usersService.updateUser(user);
    }

    // =============================
    // DELETE USER
    // =============================
    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Integer id
    ) {
        usersService.deleteUser(id);
    }

    // =============================
    // LOGIN
    // =============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users loginRequest) {
        try {
            Users user = usersService.getUserByUsername(loginRequest.getUsername());
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(401).body("Username atau password salah");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Username atau password salah");
        }
    }

    // =================================================
    // PAGINATION (PAGE + SIZE)
    // =================================================
    @GetMapping("/pagination")
    public Page<Users> getUsersPagination(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return usersService.getUsersPagination(page, size);
    }
}
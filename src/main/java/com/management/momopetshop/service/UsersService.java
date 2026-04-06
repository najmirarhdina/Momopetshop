package com.management.momopetshop.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import com.management.momopetshop.UserNotFoundException;
import com.management.momopetshop.UserAlreadyExistsException;
import com.management.momopetshop.model.Users;
import com.management.momopetshop.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // =============================
    // GET ALL USERS
    // =============================
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    // =============================
    // GET USER BY ID
    // =============================
    public Users getUserById(Integer id) {
        return usersRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User dengan ID " + id + " tidak ditemukan")
                );
    }

    // =============================
    // GET USER BY USERNAME
    // =============================
    public Users getUserByUsername(String username) {
        return usersRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("Username '" + username + "' tidak ditemukan")
                );
    }

    // =============================
    // SAVE USER
    // =============================
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }

    // =============================
    // CREATE USER
    // =============================
    public Users createUser(Users user) {
        if (usersRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username sudah digunakan");
        }
        return usersRepository.save(user);
    }
    public Users updateUser(Users user) {

    return usersRepository.save(user);

}

public void deleteUser(Integer id) {

    usersRepository.deleteById(id);

}


    // =================================================
    // ✅ PAGINATION (PAGE + SIZE SAJA)
    // =================================================
    public Page<Users> getUsersPagination(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException("Page tidak boleh kurang dari 0");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size harus lebih dari 0");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("idUser").ascending()
        );

        return usersRepository.findAll(pageable);
    }
}

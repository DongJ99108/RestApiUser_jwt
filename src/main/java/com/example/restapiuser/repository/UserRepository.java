package com.example.restapiuser.repository;

import com.example.restapiuser.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    List<UserEntity> findAllByOrderByIndateAsc();

    List<UserEntity> findByUseridContainingIgnoreCaseOrderByIndateAsc(String trim);
}

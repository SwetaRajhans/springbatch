package com.example.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.User;
import com.example.demo.model.UserContact;

public interface UserContactRepository extends JpaRepository<UserContact, String> {
}

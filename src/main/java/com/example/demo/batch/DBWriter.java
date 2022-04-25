package com.example.demo.batch;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// To write data in to DB
@Component
public class DBWriter implements ItemWriter<User> {

    private UserRepository userRepository;

	
	 @Autowired public DBWriter (UserRepository userRepository) {
	 this.userRepository = userRepository; }
	 

    @Override
    public void write(List<? extends User> users) throws Exception{
        System.out.println("Data Saved for Users: " + users);
        userRepository.saveAll(users);
        throw new Exception();
    }
}

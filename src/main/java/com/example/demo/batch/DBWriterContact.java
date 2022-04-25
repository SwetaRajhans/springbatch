package com.example.demo.batch;

import com.example.demo.model.User;
import com.example.demo.model.UserContact;
import com.example.demo.repository.UserContactRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// To write data in to DB
@Component
public class DBWriterContact implements ItemWriter<UserContact> {

    private UserContactRepository userRepository;

	
	 @Autowired public DBWriterContact (UserContactRepository userRepository) {
	 this.userRepository = userRepository; }
	 

    @Override
    public void write(List<? extends UserContact> usersContact) throws Exception{
        System.out.println("Data Saved for Users Contact : " + usersContact);
        userRepository.saveAll(usersContact);
    }
}

package com.payment.system.controllers;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.payment.system.dao.models.Role;
import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.RoleRepository;
import com.payment.system.dao.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * UserLoadService will load the csv list of users for the system, it will look for Users.csv file on the
 * classpath of the application
 */
@Service
public class UserLoadService {
    private static final Logger logger = LoggerFactory.getLogger(UserLoadService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    PasswordEncoder passEncoder;

    @PostConstruct
    private void loadUsers(){
        Resource resource = resourceLoader.getResource("classpath:Users.csv");
        try {
            for (String[] userArray : loadObjectList(resource.getFile())){
                String name =userArray[0];
                String description =userArray[1];
                String email = userArray[2];
                String password = userArray[3];
                String role = userArray[4];
                String status = userArray[5];
                User user = userRepository.findByName(name).orElse(new User(name,email,passEncoder.encode(password)));
                Role userRole = roleRepository.findByName(role).orElse(new Role(role));
                roleRepository.save(userRole);
                user.setRole(userRole);
                user.setDescription(description);
                user.setStatus(status);
                userRepository.save(user);
            }
        } catch (IOException e) {
            logger.error("CSV file failed to load", e);
        }
    }

    private static List<String[]>  loadObjectList(File file) throws IOException {
        FileReader filereader = new FileReader(file);

        // create csvReader object and skip first Line
        CSVReader csvReader = new CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build();
        return csvReader.readAll();
    }
}

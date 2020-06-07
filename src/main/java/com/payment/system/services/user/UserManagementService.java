package com.payment.system.services.user;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserLoadService will load the csv list of users for the system, it will look for Users.csv file on the
 * classpath of the application
 */
@Service
public class UserManagementService {
    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    PasswordEncoder passEncoder;

    private static String csvFile = "classpath:Users.csv";

    public String getCsvFile() { return csvFile; }

    public void setCsvFile(String csvFile) {
        UserManagementService.csvFile = csvFile;
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @PostConstruct
    public List<User> loadUsers(){
        Resource resource;
        resource = resourceLoader.getResource(csvFile);
        List<User> result=new ArrayList<>();
        try {
            for (String[] userArray : loadObjectList(resource.getFile())){
                String name =userArray[0];
                String description =userArray[1];
                String email = userArray[2];
                String password = userArray[3];
                String role = userArray[4];
                String status = userArray[5];
                User user = userRepository.findByName(name).orElse(new User(name,email,passEncoder.encode(password)));
                user.setRole(role);
                user.setDescription(description);
                user.setStatus(status);
                result.add(userRepository.save(user));
            }
        } catch (IOException e) {
            logger.error("CSV file failed to load", e);
        }
        return result;
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

package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws IOException, CsvException {
        Instant start = Instant.now();
        List<User> users = parseFile(file);
        log.info("saving list of users of size {}", users.size(), ""+Thread.currentThread().getName());
        userRepository.saveAll(users);
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        log.info("Total time elapsed {}", timeElapsed);
        return CompletableFuture.completedFuture(users);
    }

    public CompletableFuture<List<User>> findUsers() {
        log.info("getting list of users by {}", Thread.currentThread().getName());
        return CompletableFuture.completedFuture(userRepository.findAll());
    }

    private List<User> parseFile(final MultipartFile file) throws IOException, CsvException {
        List<User> list = new ArrayList<>();
        CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
        csvReader.readAll().forEach(line -> {
            User user = new User();
            user.setName(line[0]);
            user.setEmail(line[1]);
            user.setGender(line[2]);
            list.add(user);
        });
        return list;
    }
}

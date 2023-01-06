package com.javatechie.spring.batch.controller;

import com.javatechie.spring.batch.entity.Customer;
import com.javatechie.spring.batch.repository.CustomerRepository;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class BatchJobController {

    public static final String TEMP_STORAGE_PATH = "/Users/javatechie/Desktop/temp/";
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Autowired
    private CustomerRepository repository;
    @Autowired
    private JobRepository jobRepository;

    @PostMapping(path = "/importData")
    public void startBatch(@RequestParam("file") MultipartFile multipartFile) {

        try {
            String fileName = multipartFile.getOriginalFilename();
            // String newFileName= System.currentTimeMillis()+"_"+fileName;
            File fileToImport = new File(TEMP_STORAGE_PATH + fileName);
            multipartFile.transferTo(fileToImport);

            //Launch the Batch Job
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", TEMP_STORAGE_PATH + fileName)
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);

//            if (execution.getExitStatus().getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
//                //delete the file from location
//                Files.deleteIfExists(
//                        Paths.get(TEMP_STORAGE_PATH + newFileName));
//            }


        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | IOException e) {

            e.printStackTrace();
        }
    }

    @GetMapping("/customers")
    public List<Customer> getAll() {
        return repository.findAll();
    }
}

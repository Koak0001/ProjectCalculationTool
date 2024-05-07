package projectcalculationtool.repository;

import org.springframework.beans.factory.annotation.Value;

@org.springframework.stereotype.Repository
public class Repository {
    @Value("${spring.datasource.url}")
    String dbUrl;
    @Value("${spring.datasource.username}")
    String dbUsername;
    @Value("${spring.datasource.password}")
    String dbPassword;


//    Write Method - login/verifyUser
//    Write method - logout
//    Write Method - Create Project
//    Write Method - getProjects
//    Write Method - getProject
//    Write Method - getTasks
//    Write Method - getTask
//    Write Method - getUsers
//    Write Method - getHoursTask
//    Write Method - getHoursSubProject
//    Write Method - getHoursTotal
//    Write Method - getHoursPerDay
}
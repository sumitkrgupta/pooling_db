package com.PoolingAPI;


import com.PoolingAPI.Service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
@ComponentScan(basePackages = {"com.PoolingAPI"})
@EnableJpaRepositories(basePackages = {"com.PoolingAPI.Repository"})
@EnableScheduling
public class SchedulerDB {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerDB.class);
    private final EmailService emailService;
    private final PatientService patientService;
    private final ApplicationContext applicationContext;
    private final DoctorService doctorService;
    private final DrugService drugService;
    private final DrugMixService drugMixService;
    private final SigService sigService;
    private final RouteCodeService routeCodeService;
    private final List<String> patientsJobList = new ArrayList<>();
    private final List<String> doctorJobList = new ArrayList<>();
    private final List<String> drugsJobList = new ArrayList<>();
    private final List<String> drugMixJobList = new ArrayList<>();
    private final List<String> sigJobList=new ArrayList<>();
    private final List<String> routeJobList = new ArrayList<>();
    private final List<String> patientJobByID = new ArrayList<>();
    private final List<String> doctorJobByLastId = new ArrayList<>();

    @Value("${thread.corePoolSize}")
    private int corePoolSize;
    @Value("${thread.maxPoolSize}")
    private int maxPoolSize;
    @Value("${Schedule.corePoolSize}")
    private int scheduleCorePoolSize;
    @Value("${thread.QueueCapacity}")
    private int queueCapacity;
    @Value("${db.connection.error.mail}")
    private String dbFailMail;
    @Value("${thread.Asynchronous.PoolSize}")
    private int AsynchronousPoolSize;

    @Autowired
    public SchedulerDB(RouteCodeService routeCodeService, EmailService emailService, SigService sigService, DrugMixService drugMixService, DrugService drugService, DoctorService doctorService, ApplicationContext applicationContext, PatientService patientService) {
        this.routeCodeService = routeCodeService;
        this.emailService = emailService;
        this.doctorService = doctorService;
        this.applicationContext = applicationContext;
        this.patientService = patientService;
        this.drugService = drugService;
        this.drugMixService = drugMixService;
        this.sigService = sigService;
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void patientScheduledTime()  {
        try {
            logger.info("Starting Scheduled of Patients...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = patientService.patientScheduling();
                logger.info("Current Job Status : " + currentJobStatus);
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of Patients on Interval" + currentTime);
                    patientsJobList.clear();
                    logger.info("After Job successfully completed then List : " + patientsJobList);
                } else {
                    patientsJobList.add(currentTime);
                    logger.info("List of Time whenever Server Connection Lost : " + patientsJobList);
                    logger.error("Data save to Mongo DB Failed Due to Connection lost Error of Patients Service on Interval " +currentTime);
                }
            } else {
                patientsJobList.add(currentTime);
                logger.info("List of Time whenever Server Down : " + patientsJobList);
                String message = "Database connection lost for Polling DB service of Patients for Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB "+":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Service Patients " + e.getMessage(),e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void doctorScheduledTime() {
        try {
            logger.info("Starting Scheduled of Doctors...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = doctorService.doctorScheduling();
                logger.info("Current Job Status : " + currentJobStatus);
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of Doctor Service for Interval " + currentTime);
                    doctorJobList.clear();
                    logger.info("After Job successfully completed then List is: " + doctorJobList);
                } else {
                    logger.info("List of Time whenever Server Connection Lost : " + doctorJobList);
                    doctorJobList.add(currentTime);
                    logger.error("Data save to Mongo DB Failed Due to Connection lost Error of Doctor Service of Interval " +currentTime);
                }
            } else {
                doctorJobList.add(currentTime);
                logger.info("List of Time whenever Server Down : " + doctorJobList);
                String message = "Database connection lost for Polling DB service of Doctors for Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("'Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB " + ":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Doctor Service " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void drugScheduler(){
        try {
            logger.info("Starting Scheduled of Drugs...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = drugService.drugsScheduler();
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of Drugs");
                    drugsJobList.clear();
                    logger.info(String.valueOf(drugsJobList));
                } else {
                    logger.error("Data save to Mongo DB Failed Due to Some Error occur of Drug Service next Time It will cover");
                }
            } else {
                drugsJobList.add(currentTime);
                logger.info("Data save failed due to sever down of Drug Scheduler " + currentTime);
                logger.info("All Scheduler Time when data save failed due to server down " + drugsJobList);
                String message = "Database connection lost for Polling DB of Drugs service for Scheduling Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("'Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB " + ":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Service Drug " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void drugMixScheduler(){
        try {
            logger.info("Starting Scheduled of DrugsMix...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = drugMixService.drugMixScheduler();
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of DrugsMix");
                    drugMixJobList.clear();
                    logger.info(String.valueOf(drugMixJobList));
                } else {
                    drugMixJobList.add(currentTime);
                    logger.error("List of Connection Lost " + drugMixJobList);
                    logger.error("Data save to Mongo DB Failed of DrugMIx Service Due to Some Error occur or Connection Lost next Time It will cover");
                }
            } else {
                drugMixJobList.add(currentTime);
                logger.info("Data save failed due to sever down of DrugMix Scheduler " + currentTime);
                logger.info("All Scheduler Time when data save failed due to server down " + drugMixJobList);
                String message = "Database connection lost for Polling DB  of DrugsMix service for Scheduling Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("'Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB " + ":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Service DrugMix" + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InHour.time}",zone = "${spring.schedule.timeZone}")
    public void doctorScheduledByID() {
        try {
            logger.info("Starting Scheduled of Doctors...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = doctorService.doctorSchedulingByLastId();
                logger.info("Current Job Status : " + currentJobStatus);
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of Doctor Service for Interval " + currentTime);
                    doctorJobByLastId.clear();
                    logger.info("After Job successfully completed then List is: " + doctorJobByLastId);
                } else {
                    logger.info("List of Time whenever Server Connection Lost : " + doctorJobByLastId);
                    doctorJobByLastId.add(currentTime);
                    logger.error("Data save to Mongo DB Failed Due to Connection lost Error of Doctor Service of Interval " +currentTime);
                }
            } else {
                doctorJobByLastId.add(currentTime);
                logger.info("List of Time whenever Server Down : " + doctorJobByLastId);
                String message = "Database connection lost for Polling DB service of Doctors for Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("'Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB " + ":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Doctor Service " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void sigScheduler(){
        try {
            logger.info("Starting Scheduled of Sig Service...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if(status){
                boolean currentJobStatus = sigService.sigScheduler();
                if(currentJobStatus) {
                    sigJobList.clear();
                    logger.info("Sig Job List is " + sigJobList);
                    logger.info("Data save to Mongo DB Successfully of Sig Service");
                } else {
                    sigJobList.add(currentTime);
                    logger.error("List of connection lost " + sigJobList);
                    logger.error("Data save to Mongo DB Failed of Sig Service Due to Some Error occur or Connection Lost next Time It will cover");
                }
            }else {
                sigJobList.add(currentTime);
                logger.info("Data save failed due to sever down on Sig Scheduler " + currentTime);
                logger.info("All Sig Scheduler Time when data save failed due to server down" + sigJobList);
                String message = "Database connection lost for Polling DB of Sig Service for Scheduling Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("'Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB of Sig Service " + ":" + Duration.between(startTime, endTime) );

        }catch (Exception e){
            logger.error("Scheduling failed of Service Sig " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InWeek.time}",zone = "${spring.schedule.timeZone}")
    public void routeCodeScheduledTime()  {
        try {
            logger.info("Starting Scheduled of RouteCodeMastService...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = routeCodeService.routeCodeMastScheduler();
                logger.info("Current Job Status : " + currentJobStatus);
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of RouteCodeMastService on Interval" + currentTime);
                    routeJobList.clear();
                    logger.info("After Job successfully completed then List : " + routeJobList);
                } else {
                    routeJobList.add(currentTime);
                    logger.info("List of Time whenever Server Connection Lost : " + routeJobList);
                    logger.error("Data save to Mongo DB Failed Due to Connection lost Error of RouteCodeMastService Service on Interval " +currentTime);
                }
            } else {
                routeJobList.add(currentTime);
                logger.info("List of Time whenever Server Down : " + routeJobList);
                String message = "Database connection lost for Polling DB service of RouteCodeMastService for Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB "+":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Service RouteCodeMastService " + e.getMessage(),e);
        }
    }

    @Scheduled(cron = "${spring.schedule.InHour.time}",zone = "${spring.schedule.timeZone}")
    public void patientScheduledByLastID()  {
        try {
            logger.info("Starting Scheduled of Patients...................................");
            Instant startTime = Instant.now();
            Date currentDate = new Date();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = format.format(currentDate);
            boolean status = getHealthCheck();

            if (status) {
                boolean currentJobStatus = patientService.patientSchedulingById();
                logger.info("Current Job Status : " + currentJobStatus);
                if(currentJobStatus) {
                    logger.info("Data save to Mongo DB Successfully of Patients on Interval" + currentTime);
                    patientJobByID.clear();
                    logger.info("After Job successfully completed then List : " + patientJobByID);
                } else {
                    patientJobByID.add(currentTime);
                    logger.info("List of Time whenever Server Connection Lost : " + patientJobByID);
                    logger.error("Data save to Mongo DB Failed Due to Connection lost Error of Patients Service on Interval " +currentTime);
                }
            } else {
                patientJobByID.add(currentTime);
                logger.info("List of Time whenever Server Down : " + patientJobByID);
                String message = "Database connection lost for Polling DB service of Patients on the Basis of Last ID for Time " + currentTime;
                logger.error(message);
                try {
                    emailService.sendSimpleMessage(dbFailMail,"DB Connection Fail",message);
                } catch ( MailException ex ) {
                    logger.error("Database connection lost' email notification failed");
                }
            }
            Instant endTime=Instant.now();
            logger.info("Total execution time for Pooling DB "+":" + Duration.between(startTime, endTime) );
        }catch (Exception e){
            logger.error("Scheduling failed of Service Patients " + e.getMessage(),e);
        }
    }

    @Bean
    public Executor getTaskExecutor() {
        return Executors.newScheduledThreadPool(scheduleCorePoolSize);
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolExecutor=new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(corePoolSize);
        threadPoolExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolExecutor.setQueueCapacity(queueCapacity);
        return threadPoolExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(AsynchronousPoolSize);
        return threadPoolTaskScheduler;
    }

    public boolean getHealthCheck(){
        MongoHealthIndicator mhi = applicationContext.getBean(MongoHealthIndicator.class);
        Health health1 = mhi.health();
        Status status1 =health1.getStatus();
        logger.info("Mongo DB Server " + status1);

        Date currentDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = format.format(currentDate);

        if ("DOWN".equals(status1.getCode())) {
            logger.info("Server Down of any of them or both " + status1 + "Mongo DB Server " );
            logger.info("Data save failed due to sever down of Scheduler" + currentTime);
            return false;
        }else {
            logger.info("Both Server UP ");
            return  true;
        }
    }
}

package com.PoolingAPI.Service.Serviceimpl;


import com.PoolingAPI.Document.PatientsDocument;


import com.PoolingAPI.Model.Pat;
import com.PoolingAPI.Model.RequestFormat;
import com.PoolingAPI.Model.ResponseFormat;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Repository.PatientsDao;
import com.PoolingAPI.Service.PatientService;
import com.PoolingAPI.Util.Constant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    @Autowired
    private PatientService patientService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private PatientsDao patentsDao;
    @Value("${Patients.Phone.Type}")
    private String phoneType;
    @Value("${Patients.Pharmacy.ID}")
    private String SchedulerPharmacyId;
    @Value("${Pat.Count.DividedBy.In.week}")
    private int dividedBy;
    @Value("${Patient.Collection}")
    private String actualPatientsCollection;
    @Value("${Temp.Pat.CollectionName}")
    private String tempPatientsCollection;
    @Value("${spring.data.mongodb.database}")
    private String dbName;
    @Value("${patient.pooling.LastID.filePath}")
    private String filePath;
    private final Map<Integer,Integer> filterDuplicate = new HashMap<>();

    public Map<String,Object> getPatientsDataAndSaveToMongo(RequestFormat requestFormats){
        ResponseFormat responseFormat = new ResponseFormat();
        try {
            logger.info("************************ Patients Logger ************************");

            String pharmacyId = requestFormats.getPharmacyID();

            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempPatientsCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempPatientsCollection);
                if(status){
                    logger.info("Old temp Collection of Patients is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int patCount = patentsDao.getTotalCount(phoneType);
            logger.info("Total Patients Size is " +patCount);
            int batch = patCount/dividedBy;
            int skip = 0;
            int totalDataInserted=0;
            for (int i=1;i<=dividedBy+1;i++){
                List<Pat> pats = patentsDao.findPatientsData(phoneType,skip,batch);
                logger.info("Size of Patients Data " + pats.size());
                if(!pats.isEmpty()){
                    List<PatientsDocument> patientsDocuments = patientService.getMongoDBPatientsData(pats,pharmacyId,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +patientsDocuments.size());
                    totalDataInserted = totalDataInserted + patientsDocuments.size();
                    if(!patientsDocuments.isEmpty())
                    {
                        boolean success = patentsDao.sendPatientsDataToMongo(patientsDocuments,tempPatientsCollection);
                        if(success){
                            responseFormat.setStatus(Constant.successStatus);
                            responseFormat.setStatus_code(Constant.successStatusCode);
                            responseFormat.setMessage("Patients Data Inserted Successfully to MongoDB.");
                            responseFormat.setError_message(" ");
                            logger.info("Patients Data save to mongoDB Successfully " +i);
                            if(i==dividedBy+1){
                                logger.info("Patients Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualPatientsCollection);
                                if(status)
                                {
                                    logger.info("Old Patients Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualPatientsCollection);
                                    if(sure){
                                        logger.info("Old Patients Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Patients Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempPatientsCollection,actualPatientsCollection);
                                if (finalStatus){
                                    responseFormat.setStatus(Constant.successStatus);
                                    responseFormat.setStatus_code(Constant.successStatusCode);
                                    responseFormat.setMessage("Total Patients Data Inserted Successfully to MongoDB.");
                                    responseFormat.setError_message(" ");
                                    logger.info("Total Patients Data Inserted Successfully to MongoDB is " + totalDataInserted);
                                    logger.info("New Temp Collection Name Rename To Actual Patients Collection");
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            responseFormat.setStatus(Constant.failedStatus);
                            responseFormat.setStatus_code(Constant.failureStatusCode);
                            responseFormat.setMessage("Failure Patients Data save to mongoDB");
                            responseFormat.setError_message("Failed to save");
                            logger.error("Failure Patients Data save to mongoDB");
                        }
                    }
                    else {
                        logger.error("Patients Data not Converted to Document");
                    }
                }
                else {
                    responseFormat.setStatus(Constant.successStatus);
                    responseFormat.setStatus_code(Constant.successStatusCode);
                    responseFormat.setMessage("Patients Data Not found Currently on this Batch");
                    responseFormat.setError_message("No Error");
                    logger.info("Patients Data Not found Currently In Given Interval");
                }
                skip=skip+batch;
            }
            filterDuplicate.clear();
        }
        catch (Exception exception){
            filterDuplicate.clear();
            logger.error("Data Retrieve Failed or System Problem " + exception.getMessage(),exception);
            responseFormat.setStatus(Constant.failedStatus);
            responseFormat.setStatus_code(Constant.failureStatusCode);
            responseFormat.setMessage("Data Retrieve Failed or System Problem");
            responseFormat.setError_message(exception.getMessage());
        }
        return responseFormat.toResult();
    }

    //Convert Patients Entity to patients Document
    public List<PatientsDocument> getMongoDBPatientsData(List<Pat> pats, String pharmacyID,Map<Integer,Integer> filterDuplicate) {
        List<PatientsDocument> patientsDocuments = new ArrayList<>();
        try{
            for (Pat pat:pats) {
                if(filterDuplicate.containsKey(pat.getID())){

                }else {
                    PatientsDocument patientsDocument = new PatientsDocument();
                    filterDuplicate.put(pat.getID(),pat.getID());
                    patientsDocument.setPat_id(pat.getID());
                    patientsDocument.setLastname(pat.getLastName());
                    patientsDocument.setFirstname(pat.getFirstName());
                    patientsDocument.setBirthday(pat.getBirthday());
                    patientsDocument.setSex(pat.getSex());
                    patientsDocument.setAlternatelastname(pat.getAlternateLastName());
                    patientsDocument.setCreatedon(pat.getCreatedOn());
                    patientsDocument.setLastchanged(pat.getLastChanged());
                    patientsDocument.setInsertTime(Constant.getInsertTime());
                    patientsDocument.setPharmacyId(pharmacyID);
                    patientsDocument.setMatchKey(pat.getFirstName() + " " +pat.getLastName());
                    patientsDocument.setDescription(pat.getDescription());
                    patientsDocument.setPhone(pat.getPhone());
                    patientsDocuments.add(patientsDocument);
                }
            }
            logger.info("Patients Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("Patients Data not Converted to Document...." + e.getMessage(), e);
        }
        return patientsDocuments;
    }

    // This Main Function Call from Scheduler once a week
    public boolean patientScheduling(){
        logger.info("************************ Patients Logger for Scheduling ************************");
        boolean finishExecution = false;
        try {

            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempPatientsCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempPatientsCollection);
                if(status){
                    logger.info("Old temp Collection of Patients is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int patCount = patentsDao.getTotalCount(phoneType) ;
            logger.info("Total Patients Size is " +patCount);
            int batch = patCount/dividedBy;
            int skip = 0;
            int totalDataInserted=0;
            for (int i=1;i<=dividedBy+1;i++){
                List<Pat> pats = patentsDao.findPatientsData(phoneType,skip,batch);
                logger.info("Size of Patients Data " + pats.size());
                if(!pats.isEmpty()){
                    List<PatientsDocument> patientsDocuments = patientService.getMongoDBPatientsData(pats,SchedulerPharmacyId,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +patientsDocuments.size());
                    totalDataInserted = totalDataInserted + patientsDocuments.size();
                    if(!patientsDocuments.isEmpty())
                    {
                        boolean success = patentsDao.sendPatientsDataToMongo(patientsDocuments,tempPatientsCollection);
                        if(success){
                            logger.info("Patients Data save to mongoDB Successfully " +i);
                            if(i==dividedBy+1){
                                logger.info("Patients Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualPatientsCollection);
                                if(status)
                                {
                                    logger.info("Old Patients Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualPatientsCollection);
                                    if(sure){
                                        logger.info("Old Patients Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Patients Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempPatientsCollection,actualPatientsCollection);
                                if (finalStatus){
                                    finishExecution=true;
                                    logger.info("Total Patients Data Inserted Successfully to MongoDB.");
                                    logger.info("New Temp Collection Name Rename To Actual Patients Collection " +totalDataInserted);
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure Patients Data save to mongoDB");
                        }
                    }
                    else {
                        logger.error("Patients Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Patients Data Not found on this Batch " +i);
                }
                skip=skip+batch;
            }
            filterDuplicate.clear();
            return finishExecution;

        }catch (Exception e){
            filterDuplicate.clear();
            logger.error("Data Retrieve Failed or System Problem " + e.getMessage(),e);
        }
        return finishExecution;
    }

    //Main function Call from Scheduling Every 1 Hour
    public boolean patientSchedulingById(){
        logger.info("************************ Patients Logger for Scheduling ************************");
        boolean finishExecution = false;
        try {
            int lastFetchID = patientService.getLastInsertedIdFromFile(filePath);
            if(lastFetchID !=-1){
                int patCount = patentsDao.getTotalCountByLastID(phoneType,lastFetchID) ;
                logger.info("Total Patients Size is " +patCount);
                List<Pat> pats = patentsDao.findPatientsDataByGreaterThanLastId(phoneType,lastFetchID);
                if(!pats.isEmpty()){
                    List<PatientsDocument> patientsDocuments = patientService.getMongoDBPatientsData(pats,SchedulerPharmacyId,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +patientsDocuments.size());
                    if(!patientsDocuments.isEmpty())
                    {
                        boolean success = patentsDao.sendPatientsDataToMongo(patientsDocuments,actualPatientsCollection);
                        if(success){
                            finishExecution=true;
                            logger.info("Patients Data Saved to mongo Db Success in Actual Collection");
                        }
                        else {
                            logger.info("Failure Patients Data save to mongoDB may Connection Lost");
                        }
                    }
                    else {
                        logger.error("Patients Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Patients Data Not found on this Interval");
                }
            }else {
                logger.info("Latest Insert ID is Not present in file");
            }
            filterDuplicate.clear();
            return finishExecution;
        }catch (Exception e){
            filterDuplicate.clear();
            logger.error("Data Retrieve Failed or System Problem " + e.getMessage(),e);
        }
        return finishExecution;
    }

    public Integer getLastInsertedIdFromFile(String path){
        Integer lastInsertId= -1;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> map = objectMapper.readValue(new File(path), new TypeReference<Map<String, Object>>() {
            });
            if(!map.isEmpty()){
                lastInsertId = (Integer)map.get("PatientLastInsertedId");
                logger.info("Last Insert Id Is found in file " +lastInsertId);
            }
            return lastInsertId;
        }catch (IOException e){
            logger.error("File Not found Exception");
        }
        return lastInsertId;
    }
}

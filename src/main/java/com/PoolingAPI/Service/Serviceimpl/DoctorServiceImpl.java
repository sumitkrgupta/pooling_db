package com.PoolingAPI.Service.Serviceimpl;

import com.PoolingAPI.Document.DoctorDocument;
import com.PoolingAPI.Model.*;
import com.PoolingAPI.Repository.DoctorsDao;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Service.DoctorService;
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
public class DoctorServiceImpl implements DoctorService{

    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private DoctorsDao doctorsDao;
    @Value("${Doctor.Phone.Type}")
    private String phoneType;
    @Value("${Doctor.Pharmacy.ID}")
    private String SchedulerPharmacyId;
    @Value("${Doctor.Collection}")
    private String actualDoctorCollection;
    @Value("${Doc.Count.DividedBy.In.Week}")
    private int dividedBy;
    @Value("${Temp.Doc.CollectionName}")
    private String tempDocCollectionName;
    @Value("${spring.data.mongodb.database}")
    private String dbName;
    @Value("${doctor.pooling.LastID.filePath}")
    private String filePath;
    private final Map<Integer,Integer> filterDuplicate = new HashMap<>();

    /**
     * @param requestFormats : Pharmacy Id request body
     */
    public Map<String,Object> getDoctorsDataAndSaveToMongo(RequestFormat requestFormats){
        ResponseFormat responseFormat = new ResponseFormat();
        logger.info("************************ Doctors Logger ************************");
        try {
            String pharmacyID = requestFormats.getPharmacyID();
            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempDocCollectionName);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDocCollectionName);
                if(status){
                    logger.info("Old temp Collection of Doctors is Drop");
                }else {
                    logger.info("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int docCount = doctorsDao.getTotalCount(phoneType);
            logger.info("Total Patients Size is " +docCount);
            int batch = docCount/dividedBy;
            int skip = 0;
            int totalDataInserted=0;
            for (int i=1;i<=dividedBy+1;i++){
                List<Doc> docs = doctorsDao.findDoctorsData(phoneType,skip,batch);
                logger.info("Size of Doctors Data " + docs.size());
                if(!docs.isEmpty()){
                    List<DoctorDocument> doctorDocuments = doctorService.getMongoDBDoctorsData(docs,pharmacyID,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +doctorDocuments.size());
                    totalDataInserted = totalDataInserted + +doctorDocuments.size();
                    if(!doctorDocuments.isEmpty())
                    {
                        boolean success = doctorsDao.sendDoctorsDataToMongo(doctorDocuments,tempDocCollectionName);
                        if(success){
                            responseFormat.setStatus(Constant.successStatus);
                            responseFormat.setStatus_code(Constant.successStatusCode);
                            responseFormat.setMessage("Doctors Data Inserted Successfully to MongoDB." +i);
                            responseFormat.setError_message(" ");
                            logger.info("Doctors Data save to mongoDB Successfully " +i);
                            if(i==dividedBy+1){
                                logger.info("Doctors Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualDoctorCollection);
                                if(status)
                                {
                                    logger.info("Old Doctors Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDoctorCollection);
                                    if(sure){
                                        logger.info("Old Doctors Collection Drop");
                                    }else {
                                        logger.info("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Doctors Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempDocCollectionName,actualDoctorCollection);
                                if (finalStatus){
                                    responseFormat.setStatus(Constant.successStatus);
                                    responseFormat.setStatus_code(Constant.successStatusCode);
                                    responseFormat.setMessage("Total Doctors Data Inserted Successfully to MongoDB is " +docCount );
                                    responseFormat.setError_message(" ");
                                    logger.info("Total Doctors Data save to mongoDB Successfully is " + totalDataInserted);
                                    logger.info("New Temp Collection Name Rename To Actual Doctors Collection");
                                }else {
                                    logger.info("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            responseFormat.setStatus(Constant.failedStatus);
                            responseFormat.setStatus_code(Constant.failureStatusCode);
                            responseFormat.setMessage("Failure Doctors Data save to mongoDB");
                            responseFormat.setError_message("Failed to save May Be connection Lost");
                            logger.info("Failure Doctors Data save to mongoDB May Be connection Lost");
                        }
                    }
                    else {

                        logger.error("Doctors Data not Converted to Document");
                    }
                }
                else {
                    responseFormat.setStatus(Constant.successStatus);
                    responseFormat.setStatus_code(Constant.successStatusCode);
                    responseFormat.setMessage("Doctors Data Not found on this Batch " +i);
                    responseFormat.setError_message("No Error");
                    logger.info("Doctors Data Not found on this Batch " +i);
                }
                skip=skip+batch;
            }
            filterDuplicate.clear();
        }
        catch (Exception e){
            filterDuplicate.clear();
            logger.error(e.getMessage(),e);
            responseFormat.setStatus(Constant.failedStatus);
            responseFormat.setStatus_code(Constant.failureStatusCode);
            responseFormat.setMessage("Connection Lost or System Problem ");
            responseFormat.setError_message(e.getMessage());
        }
        return responseFormat.toResult();
    }

    /**
     * @param docs : take Doctor List
     * @param pharmacyID : Pharmacy Id
     */
    public List<DoctorDocument> getMongoDBDoctorsData(List<Doc> docs,String pharmacyID,Map<Integer,Integer> filterDuplicate){
        List<DoctorDocument> doctorDocuments =new ArrayList<>();
        try{
            for (Doc doc:docs) {
                if(filterDuplicate.containsKey(doc.getID())){

                }else {
                    DoctorDocument doctorDocument = new DoctorDocument();
                    filterDuplicate.put(doc.getID(),doc.getID());
                    doctorDocument.setDoc_id(doc.getID());
                    doctorDocument.setLastname(doc.getLastName());
                    doctorDocument.setFirstname(doc.getFirstName());
                    doctorDocument.setSalutation(doc.getSalutation());
                    doctorDocument.setDesignation(doc.getDesignation());
                    doctorDocument.setSpecialty(doc.getSpecialty());
                    doctorDocument.setCreated(doc.getCreated());
                    doctorDocument.setChanged(doc.getChanged());
                    doctorDocument.setDispensingrights(doc.getDispensingRights());
                    doctorDocument.setActive(doc.isActive());
                    doctorDocument.setInsertTime(Constant.getInsertTime());
                    doctorDocument.setPharmacyId(pharmacyID);
                    doctorDocument.setMatchKey(doc.getFirstName() + " " +doc.getLastName());
                    doctorDocument.setLicence_Number(doc.getLicence1());
                    doctorDocument.setDescription(doc.getDescription());
                    doctorDocument.setPhone(doc.getPhone());
                    doctorDocuments.add(doctorDocument);
                }
            }
            logger.info("Doctors Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("Doctors Data not Converted to Document...." + e.getMessage(),e);
        }
        return doctorDocuments;
    }

    /**
     * function call from scheduler
     */
    public boolean doctorScheduling(){
        logger.info("************************ Doctors Logger for Scheduling ************************");
        try {
            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempDocCollectionName);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDocCollectionName);
                if(status){
                    logger.info("Old temp Collection of Doctors is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int docCount = doctorsDao.getTotalCount(phoneType) ;
            logger.info("Total Patients Size is " +docCount);
            int batch = docCount/dividedBy;
            int skip = 0;
            int totalDataInserted=0;
            for (int i=1;i<=dividedBy+1;i++){
                List<Doc> docs = doctorsDao.findDoctorsData(phoneType,skip,batch);
                logger.info("Size of Doctors Data " + docs.size());
                if(!docs.isEmpty()){
                    List<DoctorDocument> doctorDocuments = doctorService.getMongoDBDoctorsData(docs,SchedulerPharmacyId,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +doctorDocuments.size());
                    totalDataInserted = totalDataInserted + doctorDocuments.size();
                    if(!doctorDocuments.isEmpty())
                    {
                        boolean success = doctorsDao.sendDoctorsDataToMongo(doctorDocuments,tempDocCollectionName);
                        if(success){
                            logger.info("Doctors Data save to mongoDB Successfully Batch count is " +i);
                            if(i==dividedBy+1){
                                logger.info("Doctors Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualDoctorCollection);
                                if(status)
                                {
                                    logger.info("Old Doctors Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDoctorCollection);
                                    if(sure){
                                        logger.info("Old Doctors Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Doctors Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempDocCollectionName,actualDoctorCollection);
                                if (finalStatus){
                                    logger.info("Total Doctors Data save to mongoDB Successfully is " + totalDataInserted);
                                    logger.info("New Temp Collection Name Rename To Actual Doctors Collection");
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure Doctors Data save to mongoDB");
                        }
                    }
                    else {

                        logger.error("Doctors Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Doctors Data Not found on this Batch count is " +i);
                }
                skip=skip+batch;
            }
            filterDuplicate.clear();
            return true;
        }
        catch (Exception e){
            filterDuplicate.clear();
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * function call from scheduler
     */
    public boolean doctorSchedulingByLastId(){
        logger.info("************************ Doctors Logger for Scheduling ************************");
        boolean finishExecution = false;
        try {
            Integer lastFetchID = doctorService.getLastInsertedIdFromFile(filePath);
            if(lastFetchID!=-1){
                Integer docCount = doctorsDao.getTotalCountByLastID(phoneType,lastFetchID) ;
                logger.info("Total Doctor count is " +docCount);
                List<Doc> docs = doctorsDao.findDoctorDataByGreaterThanLastId(phoneType,lastFetchID);
                if(!docs.isEmpty()){
                    List<DoctorDocument> doctorDocuments = doctorService.getMongoDBDoctorsData(docs,SchedulerPharmacyId,filterDuplicate);
                    logger.info("Size of Doctors After Filter " +doctorDocuments.size());
                    if(!doctorDocuments.isEmpty())
                    {
                        boolean success = doctorsDao.sendDoctorsDataToMongo(doctorDocuments,actualDoctorCollection);
                        if(success){
                            finishExecution=true;
                            logger.info("Doctors Data Saved to mongo Db Success in Actual Collection");
                        }
                        else {
                            logger.info("Failure Doctors Data save to mongoDB may be Connection Lost");
                        }
                    }
                    else {

                        logger.error("Doctors Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Doctors Data Not found on This Time ");
                }
            }else {
                logger.info("Latest Insert ID is Not present in file");
            }
            filterDuplicate.clear();
            return finishExecution;
        }
        catch (Exception e){
            filterDuplicate.clear();
            logger.error(e.getMessage(),e);
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
                lastInsertId = (Integer) map.get("DoctorLastInsertedId");
                logger.info("Last Insert Id Is found in file " +lastInsertId);
            }
            return lastInsertId;
        }catch (IOException e){
            logger.error("File Not found Exception");
        }
        return lastInsertId;
    }
}

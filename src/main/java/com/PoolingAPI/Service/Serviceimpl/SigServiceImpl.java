package com.PoolingAPI.Service.Serviceimpl;


import com.PoolingAPI.Document.SigDocument;
import com.PoolingAPI.Model.*;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Repository.SigDao;
import com.PoolingAPI.Service.SigService;
import com.PoolingAPI.Util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SigServiceImpl implements SigService {

    private static final Logger logger = LoggerFactory.getLogger(SigServiceImpl.class);
    @Autowired
    private SigService sigService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private SigDao sigDao;
    @Value("${Sig.Lang.Type}")
    private String SigLang;
    @Value("${Sig.Count.Divided}")
    private int dividedBy;
    @Value("${spring.data.mongodb.database}")
    private String DBName;
    @Value("${Actual.Sig.CollectionName}")
    private String actualSigCollection;
    @Value("${Temp.Sig.CollectionName}")
    private String tempSigCollection;
    @Value("${Sig.Pharmacy.ID}")
    private String sigPharmacyID;

    public Map<String,Object> getSigDataAndSaveToMongo(RequestFormat requestFormats){
        ResponseFormat responseFormat = new ResponseFormat();
        try {
            logger.info("************************ Sig Logger ************************");
            String pharmacyID = requestFormats.getPharmacyID();

            //check rename collection exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(DBName,tempSigCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempSigCollection);
                if(status){
                    logger.info("Old temp Collection of Sig is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int sigCount = sigDao.getTotalCount(SigLang);
            logger.info("Total Number of Sig " + sigCount);
            int batch = sigCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<Sig> sigData = sigDao.getSigData(SigLang,skip,batch);
                logger.info("Size of Sig Data " + sigData.size());
                if(!sigData.isEmpty()){
                    List<SigDocument> sigDocuments = sigService.getMongoDBSigData(sigData,pharmacyID);
                    if(!sigDocuments.isEmpty())
                    {
                        boolean success = sigDao.sendSigDataToMongo(sigDocuments,tempSigCollection);
                        if(success){
                            responseFormat.setStatus(Constant.successStatus);
                            responseFormat.setStatus_code(Constant.successStatusCode);
                            responseFormat.setMessage("Sig Data Inserted Successfully to MongoDB.");
                            responseFormat.setError_message(" ");
                            logger.info("Sig Data save to mongoDB Successfully " +i);

                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(DBName,actualSigCollection);
                                if(status)
                                {
                                    logger.info("Old Sig Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualSigCollection);
                                    if(sure){
                                        logger.info("Old Sig Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Sig Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(DBName,tempSigCollection,actualSigCollection);
                                if (finalStatus){
                                    responseFormat.setStatus(Constant.successStatus);
                                    responseFormat.setStatus_code(Constant.successStatusCode);
                                    responseFormat.setMessage("Total Sig Data Inserted Successfully to MongoDB.");
                                    responseFormat.setError_message(" ");
                                    logger.info("Total Sig Data Inserted Successfully to MongoDB.");
                                    logger.info("New Temp Collection Name Rename To Actual Sig Collection");
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            responseFormat.setStatus(Constant.failedStatus);
                            responseFormat.setStatus_code(Constant.failureStatusCode);
                            responseFormat.setMessage("Failure Sig Data save to mongoDB or Database Connection Lost");
                            responseFormat.setError_message("Failed to save");
                            logger.error("Failure Sig Data save to mongoDB or Database Connection Lost");
                        }
                    }
                    else {
                        logger.error("Sig Data not Converted to Document");
                    }
                }
                else {
                    responseFormat.setStatus(Constant.successStatus);
                    responseFormat.setStatus_code(Constant.successStatusCode);
                    responseFormat.setMessage("Sig Data Not found In this Batch " +i);
                    responseFormat.setError_message("No Error");
                    logger.info("Sig Data Not found In this Batch " +i);
                }
                skip=skip+batch;
            }

        }
        catch (Exception e){
            logger.error("Data Retrieve Failed or Database connection Lost " + e.getMessage(),e);
            responseFormat.setStatus(Constant.failedStatus);
            responseFormat.setStatus_code(Constant.failureStatusCode);
            responseFormat.setMessage("Data Retrieve Failed or Database connection Lost");
            responseFormat.setError_message(e.getMessage());
        }
        return responseFormat.toResult();
    }

    public List<SigDocument> getMongoDBSigData(List<Sig> sigs, String pharmacyID){

        List<SigDocument> sigDocuments =new ArrayList<>();
        try {
            for (Sig sig:sigs){
                SigDocument sigDocument = new SigDocument();
                sigDocument.setSig_id(sig.getId());
                sigDocument.setToken(sig.getToken());
                sigDocument.setSig_text(sig.getText());
                sigDocument.setIs_route_of_admin(sig.getIsRouteOfAdmin());
                sigDocument.setPharmacy_ID(pharmacyID);
                sigDocument.setInsertTime(Constant.getInsertTime());
                sigDocuments.add(sigDocument);
            }
            logger.info("Sig Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("Sig Data not Converted to Document...." + e.getMessage(),e);
        }
        return sigDocuments;
    }

    //Drugs Scheduler
    public boolean sigScheduler(){
        boolean finishCheck=false;
        try {
            logger.info("************************ Sig Logger ************************");
            //check rename collection exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(DBName,tempSigCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempSigCollection);
                if(status){
                    logger.info("Old temp Collection of Sig is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int sigCount = sigDao.getTotalCount(SigLang);
            logger.info("Total Number of Sig " + sigCount);
            int batch = sigCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<Sig> sigData = sigDao.getSigData(SigLang,skip,batch);
                logger.info("Size of Sig Data " + sigData.size());
                if(!sigData.isEmpty()){
                    List<SigDocument> sigDocuments = sigService.getMongoDBSigData(sigData,sigPharmacyID);
                    if(!sigDocuments.isEmpty())
                    {
                        boolean success = sigDao.sendSigDataToMongo(sigDocuments,tempSigCollection);
                        if(success){
                            logger.info("Sig Data save to mongoDB Successfully ");
                            if(i==dividedBy+1){
                                logger.info("Total Sig Data Inserted Successfully to MongoDB.");
                                boolean status = mongoDbDao.collectionExist(DBName,actualSigCollection);
                                if(status)
                                {
                                    logger.info("Old Sig Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualSigCollection);
                                    if(sure){
                                        logger.info("Old Sig Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Sig Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(DBName,tempSigCollection,actualSigCollection);
                                if (finalStatus){
                                    logger.info("Total Sig Data Inserted Successfully to MongoDB.");
                                    logger.info("New Temp Collection Name Rename To Actual Sig Collection " +sigCount);
                                    finishCheck=true;
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure Sig Data save to mongoDB");
                        }
                    }
                    else {

                        logger.error("Sig Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Sig Data Not found Currently Given Time");
                }
                skip=skip+batch;
            }
            return finishCheck;
        }
        catch (Exception e){
            logger.error("Data Retrieve Failed or System Problem " + e.getMessage(),e);
        }
        return finishCheck;
    }
}

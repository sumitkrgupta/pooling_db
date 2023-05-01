package com.PoolingAPI.Service.Serviceimpl;

import com.PoolingAPI.Document.DrugMixDocument;
import com.PoolingAPI.Model.DrgMix;
import com.PoolingAPI.Model.RequestFormat;
import com.PoolingAPI.Model.ResponseFormat;
import com.PoolingAPI.Repository.DrugMixDao;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Service.DrugMixService;
import com.PoolingAPI.Util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DrugMixServiceImpl implements DrugMixService {

    private static final Logger logger = LoggerFactory.getLogger(DrugMixServiceImpl.class);
    @Autowired
    private DrugMixService drugMixService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private DrugMixDao drugMixDao;
    @Value("${DrugMix.Count.Divided}")
    private int dividedBy;
    @Value("${spring.data.mongodb.database}")
    private String DBName;
    @Value("${Actual.DrugMix.CollectionName}")
    private String actualDrugMixCollection;
    @Value("${Temp.DrugMix.CollectionName}")
    private String tempDrugMixCollection;
    @Value("${DrugMix.Pharmacy.ID}")
    private String drugMixPharmacyID;


    public Map<String,Object> getDrugsMixDataAndSaveToMongo(RequestFormat requestFormats){
        ResponseFormat responseFormat = new ResponseFormat();
        try {
            logger.info("************************ Drugs Mix Logger ************************");
            String pharmacyID = requestFormats.getPharmacyID();
            //check old Temp collection exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(DBName,tempDrugMixCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDrugMixCollection);
                if(status){
                    logger.info("Old temp Collection of DrugMix is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int drugMixCount = drugMixDao.getTotalCount();
            logger.info("Total Number of Drugs " + drugMixCount);
            int batch = drugMixCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<DrgMix> drgMixes = drugMixDao.getDrugsMixData(skip,batch);
                logger.info("Size of DrugMix Data " + drgMixes.size());
                if (!drgMixes.isEmpty()){
                    List<DrugMixDocument> drugMixDocuments = drugMixService.getMongoDBDrugMixData(drgMixes,pharmacyID);
                    if(!drugMixDocuments.isEmpty())
                    {
                        boolean success = drugMixDao.sendDrugsMixDataToMongo(drugMixDocuments,tempDrugMixCollection);
                        if(success){
                            responseFormat.setStatus(Constant.successStatus);
                            responseFormat.setStatus_code(Constant.successStatusCode);
                            responseFormat.setMessage("DrugsMix Data Inserted Successfully to MongoDB." +i);
                            responseFormat.setError_message(" ");
                            logger.info("DrugsMix Data save to mongoDB Successfully " + i);
                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(DBName,actualDrugMixCollection);
                                if(status)
                                {
                                    logger.info("Old DrugMix Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDrugMixCollection);
                                    if(sure){
                                        logger.info("Old DrugMix Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD DrugMix Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(DBName,tempDrugMixCollection,actualDrugMixCollection);
                                if (finalStatus){
                                    responseFormat.setStatus(Constant.successStatus);
                                    responseFormat.setStatus_code(Constant.successStatusCode);
                                    responseFormat.setMessage("Total DrugsMix Data Inserted Successfully to MongoDB is " +drugMixCount);
                                    responseFormat.setError_message(" ");
                                    logger.info("Total DrugsMix Data Inserted Successfully to MongoDB.");
                                    logger.info("New Temp Collection Name Rename To Actual DrugMix Collection");
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            responseFormat.setStatus(Constant.failedStatus);
                            responseFormat.setStatus_code(Constant.failureStatusCode);
                            responseFormat.setMessage("Failure DrugsMix Data save to mongoDB on Batch" +i);
                            responseFormat.setError_message("Failed to save either server Connection Lost or System Problem");
                            logger.error("Failure DrugsMix Data save to mongoDB due to connection lost");
                        }
                    }
                    else {
                        logger.error("DrugsMix Data not Converted to Document");
                    }
                }
                else {
                    responseFormat.setStatus(Constant.successStatus);
                    responseFormat.setStatus_code(Constant.successStatusCode);
                    responseFormat.setMessage("DrugsMix Data Not found");
                    responseFormat.setError_message("No Error");
                    logger.info("DrugsMix Data Not found ");
                }
                skip=skip+batch;
            }
        }
        catch (Exception e)
        {
            logger.error("DrugMix Data Insertion Failed or Connection Lost " + e.getMessage(),e);
            responseFormat.setStatus(Constant.failedStatus);
            responseFormat.setStatus_code(Constant.failureStatusCode);
            responseFormat.setMessage("Data Retrieve Failed or System Problem");
            responseFormat.setError_message(e.getMessage());
        }
        return responseFormat.toResult();
    }

    //Convert Patients Entity to patients Document
    public List<DrugMixDocument> getMongoDBDrugMixData(List<DrgMix> drgMixes, String pharmacyID){
        List<DrugMixDocument> drugMixDocuments =new ArrayList<>();
        try {
            for (DrgMix drgMix : drgMixes){
                DrugMixDocument drugMixDocument = new DrugMixDocument();
                drugMixDocument.setMix_id(drgMix.getID());
                drugMixDocument.setMix_description(drgMix.getDescription());
                drugMixDocument.setDrug_mix_form_id(drgMix.getDrgFormId());
                drugMixDocument.setPharmacyID(pharmacyID);
                drugMixDocument.setInsertTime(Constant.getInsertTime());
                drugMixDocuments.add(drugMixDocument);
            }
            logger.info("DrugsMix Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("DrugsMix Data not Converted to Document...." + e.getMessage(),e);
        }
        return drugMixDocuments;
    }

    //Drugs Scheduler
    public boolean drugMixScheduler(){
        boolean finishCheck=false;
        try {
            logger.info("************************ Drugs Mix Logger Scheduler ************************");
            //check Temp collection exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(DBName,tempDrugMixCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDrugMixCollection);
                if(status){
                    logger.info("Old temp Collection of DrugMix is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }
            int drugMixCount = drugMixDao.getTotalCount();
            logger.info("Total Number of DrugMix " + drugMixCount);
            int batch = drugMixCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<DrgMix> drgMixes = drugMixDao.getDrugsMixData(skip,batch);
                logger.info("Size of DrugMix Data in" + i + " " + drgMixes.size());
                if (!drgMixes.isEmpty()){
                    List<DrugMixDocument> drugMixDocuments = drugMixService.getMongoDBDrugMixData(drgMixes,drugMixPharmacyID);
                    if(!drugMixDocuments.isEmpty())
                    {
                        boolean success = drugMixDao.sendDrugsMixDataToMongo(drugMixDocuments,tempDrugMixCollection);
                        if(success){
                            logger.info("DrugsMix Data save to mongoDB Successfully " +i);
                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(DBName,actualDrugMixCollection);
                                if(status)
                                {
                                    logger.info("Old DrugMix Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDrugMixCollection);
                                    if(sure){
                                        logger.info("Old DrugMix Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD DrugMix Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(DBName,tempDrugMixCollection,actualDrugMixCollection);
                                if (finalStatus){
                                    logger.info("Total DrugsMix Data Inserted Successfully to MongoDB is " +drugMixCount);
                                    logger.info("New Temp Collection Name Rename To Actual DrugMix Collection");
                                    finishCheck=true;
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure DrugsMix Data save to mongoDB or connection lost " +i);
                        }
                    }
                    else {
                        logger.error("DrugsMix Data not Converted to Document");
                    }
                }
                else {
                    logger.info("DrugsMix Data Not found "+i);
                }
                skip=skip+batch;
            }
            return finishCheck;
        }
        catch (Exception e)
        {
            logger.error("DrugMix Data Insertion Failed or Connection Lost " + e.getMessage(),e);
            return false;
        }
    }
}

package com.PoolingAPI.Service.Serviceimpl;

import com.PoolingAPI.Document.DrugDocument;
import com.PoolingAPI.Model.Drg;
import com.PoolingAPI.Model.RequestFormat;
import com.PoolingAPI.Model.ResponseFormat;
import com.PoolingAPI.Repository.DrugsDao;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Service.DrugService;
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
public class DrugServiceImpl implements DrugService {

    private static final Logger logger = LoggerFactory.getLogger(DrugServiceImpl.class);
    @Autowired
    private DrugService drugService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private DrugsDao drugsDao;
    @Value("${Drug.Count.DividedBy}")
    private int dividedBy;
    @Value("${spring.data.mongodb.database}")
    private String dbName;
    @Value("${Actual.Drug.CollectionName}")
    private String actualDrugCollection;
    @Value("${Temp.Drug.CollectionName}")
    private String tempDrugCollection;
    @Value("${Drugs.Pharmacy.ID}")
    private String drugPharmacyID;
    boolean drugPackActive = true;

    public Map<String,Object> getDrugsDataAndSaveToMongo(RequestFormat requestFormats){
        ResponseFormat responseFormat = new ResponseFormat();
        try {
            logger.info("************************ Drugs Logger ************************");
            String pharmacyID = requestFormats.getPharmacyID();
            //check collection rename exist logic
            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempDrugCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDrugCollection);
                if(status){
                    logger.info("Old temp Collection of Drug is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int drugCount = drugsDao.getTotalCount(drugPackActive);
            logger.info("Total Number of Drugs " + drugCount);
            int batch = drugCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<Drg> drugs = drugsDao.getDrugsDataInChunk(drugPackActive,skip,batch);
                logger.info("Size of Drugs Data " + drugs.size());
                if (!drugs.isEmpty()){
                    List<DrugDocument> drugDocuments = drugService.getMongoDBDrugsData(drugs,pharmacyID);
                    if(!drugDocuments.isEmpty())
                    {
                        boolean success = drugsDao.sendDrugsDataToMongo(drugDocuments,tempDrugCollection);
                        if(success){
                            responseFormat.setStatus(Constant.successStatus);
                            responseFormat.setStatus_code(Constant.successStatusCode);
                            responseFormat.setMessage("Drugs Data Inserted to MongoDB in Batch");
                            responseFormat.setError_message(" ");
                            logger.info("Drugs Data save to mongoDB Successfully and count is" +i);
                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualDrugCollection);
                                if(status)
                                {
                                    logger.info("Old Drug Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDrugCollection);
                                    if(sure){
                                        logger.info("Old Drug Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Drug Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempDrugCollection,actualDrugCollection);
                                if (finalStatus){
                                    responseFormat.setStatus(Constant.successStatus);
                                    responseFormat.setStatus_code(Constant.successStatusCode);
                                    responseFormat.setMessage("Total Drugs Data Inserted Successfully to MongoDB is " + drugCount);
                                    responseFormat.setError_message(" ");
                                    logger.info("Total Drugs Data Inserted Successfully to MongoDB.");
                                    logger.info("New Temp Collection Name Rename To Actual Drug Collection");
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            responseFormat.setStatus(Constant.failedStatus);
                            responseFormat.setStatus_code(Constant.failureStatusCode);
                            responseFormat.setMessage("Failure Drugs Data save to mongoDB" +i);
                            responseFormat.setError_message("Failed to save");
                            logger.error("Failure Drugs Data save to mongoDB or Connection Lost");
                        }
                    }
                    else {
                        logger.error("Drugs Data not Converted to Document");
                    }
                }
                else {
                    responseFormat.setStatus(Constant.successStatus);
                    responseFormat.setStatus_code(Constant.successStatusCode);
                    responseFormat.setMessage("Drugs Data Not found following in Batch");
                    responseFormat.setError_message("No Error");
                    logger.info("Drugs Data Not found following Batch " +i);
                }
                skip=skip+batch;
            }
        }
        catch (Exception e)
        {
            logger.error("Database Connection failed or System problem " + e.getMessage(),e);
            responseFormat.setStatus(Constant.failedStatus);
            responseFormat.setStatus_code(Constant.failureStatusCode);
            responseFormat.setMessage("Data Retrieve Failed or System Problem");
            responseFormat.setError_message(e.getMessage());
        }
        return responseFormat.toResult();
    }

    //Convert Drugs Entity to Drugs Document
    public List<DrugDocument> getMongoDBDrugsData(List<Drg> drugs, String pharmacyID){
        List<DrugDocument> drugDocuments =new ArrayList<>();
        try {
            for (Drg drug:drugs){
                DrugDocument drugDocument = new DrugDocument();
                drugDocument.setDrug_id(drug.getDrgId());
                drugDocument.setDrugPackID(drug.getDrgPackID());
                drugDocument.setDrugPackActive(drug.getDrgPackActive());
                drugDocument.setDrug_active(drug.getDrgActive());
                drugDocument.setPackSize(drug.getPackSize());
                drugDocument.setDin(drug.getDIN());
                drugDocument.setBrand_name(drug.getBrandName());
                drugDocument.setGeneric_name(drug.getGenericName());
                drugDocument.setStrength(drug.getStrength());
                drugDocument.setEquivalent_to(drug.getEquivTo());
                drugDocument.setBrand_generic_type(drug.getBrandGenericType());
                drugDocument.setDefault_sig(drug.getDefaultSig());
                drugDocument.setDrugForm(drug.getDrugForm());
                drugDocument.setForm_id(drug.getDrgFormId());
                drugDocument.setShape_id(drug.getShapeId());
                drugDocument.setPharmacyID(pharmacyID);
                drugDocument.setInsertTime(Constant.getInsertTime());

                drugDocuments.add(drugDocument);
            }
            logger.info("Drugs Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("Drugs Data not Converted to Document...." + e.getMessage(),e);
        }
        return drugDocuments;
    }

    //Drugs Scheduler
    public boolean drugsScheduler(){
        boolean finishScheduler = false;
        try {
            logger.info("************************ Drugs Logger Scheduler ************************");
            //check collection rename exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(dbName,tempDrugCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempDrugCollection);
                if(status){
                    logger.info("Old temp Collection of Drug is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }

            int drugCount = drugsDao.getTotalCount(drugPackActive);
            logger.info("Total Number of Drugs " + drugCount);
            int batch = drugCount/dividedBy;
            int skip = 0;
            for(int i=1;i<=dividedBy+1;i++){
                List<Drg> drugs = drugsDao.getDrugsDataInChunk(drugPackActive,skip,batch);
                logger.info("Size of Drugs Data " + drugs.size());
                if (!drugs.isEmpty()){
                    List<DrugDocument> drugDocuments = drugService.getMongoDBDrugsData(drugs,drugPharmacyID);
                    if(!drugDocuments.isEmpty())
                    {
                        boolean success = drugsDao.sendDrugsDataToMongo(drugDocuments,tempDrugCollection);
                        if(success){
                            logger.info("Drugs Data save to mongoDB Successfully and count is" +i);
                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(dbName,actualDrugCollection);
                                if(status)
                                {
                                    logger.info("Old Drug Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualDrugCollection);
                                    if(sure){
                                        logger.info("Old Drug Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD Drug Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(dbName,tempDrugCollection,actualDrugCollection);
                                if (finalStatus){
                                    logger.info("Total Drugs Data Inserted Successfully to MongoDB is " +drugCount);
                                    logger.info("New Temp Collection Name Rename To Actual Drug Collection");
                                    finishScheduler=true;
                                }else {
                                    logger.error("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure Drugs Data save to mongoDB");
                        }
                    }
                    else {
                        logger.error("Drugs Data not Converted to Document");
                    }
                }
                else {
                    logger.info("Drugs Data Not found" +i);
                }
                skip=skip+batch;
            }
            return finishScheduler;
        }
        catch (Exception e)
        {
            logger.error("Data not Saved Connection Lost or System Problem " + e.getMessage(),e);
        }
        return finishScheduler;
    }
}

package com.PoolingAPI.Service.Serviceimpl;

import com.PoolingAPI.Document.RouteCodeMastDocument;
import com.PoolingAPI.Model.RouteCodeMast;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Repository.RouteCodeMastDao;
import com.PoolingAPI.Service.RouteCodeService;
import com.PoolingAPI.Util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteCodeServiceImpl implements RouteCodeService {

    private static final Logger logger = LoggerFactory.getLogger(RouteCodeServiceImpl.class);
    @Autowired
    private RouteCodeService routeCodeService;
    @Autowired
    private MongoDbDao mongoDbDao;
    @Autowired
    private RouteCodeMastDao routeCodeMastDao;
    @Value("${RouteCodeMast.Count.Divided}")
    private int dividedBy;
    @Value("${spring.data.mongodb.database}")
    private String DBName;
    @Value("${Actual.RouteCodeMast.CollectionName}")
    private String actualRouteCodeMastCollection;
    @Value("${Temp.RouteCodeMast.CollectionName}")
    private String tempRouteCodeMastCollection;
    @Value("${RouteCodeMast.Pharmacy.ID}")
    private String routeCodeMastPharmacyID;


    //Drugs Scheduler
    public boolean routeCodeMastScheduler(){

        boolean finishCheck=false;
        try {
            logger.info("************************ RouteCodeMast Logger Scheduler ************************");
            //check Temp collection exist
            boolean checkRenameCollection=mongoDbDao.collectionExist(DBName,tempRouteCodeMastCollection);
            if(checkRenameCollection){
                boolean status=mongoDbDao.dropExistingCollection(tempRouteCodeMastCollection);
                if(status){
                    logger.info("Old temp Collection of routeCodeMastList is Drop");
                }else {
                    logger.error("Database Connection Lost or System Problem");
                }
            }else {
                logger.info("Old Rename Collection Not Exist");
            }
            int routeCodeCount = routeCodeMastDao.getTotalCount();
            logger.info("Total Number of routeCode " + routeCodeCount);
            int batch = routeCodeCount/dividedBy;
            int skip = 0;

            for(int i=1;i<=dividedBy+1;i++){
                List<RouteCodeMast> routeCodeMastList = routeCodeMastDao.findRouteCodeMast(skip,batch);
                logger.info("Size of RouteCodeMast Data in " + i + " is " + routeCodeMastList.size());
                if (!routeCodeMastList.isEmpty()){
                    List<RouteCodeMastDocument> routeCodeMastDocuments = routeCodeService.getMongoDBRouteCode(routeCodeMastList,routeCodeMastPharmacyID);
                    if(!routeCodeMastDocuments.isEmpty())
                    {
                        boolean success = routeCodeMastDao.sendRouteDataToMongo(routeCodeMastDocuments,tempRouteCodeMastCollection);
                        if(success){
                            logger.info("RouteCodeMast Data save to mongoDB Successfully " +i);
                            if(i==dividedBy+1){
                                logger.info("Data Saved to mongo Db Success in Temp Collection");
                                boolean status = mongoDbDao.collectionExist(DBName,actualRouteCodeMastCollection);
                                if(status)
                                {
                                    logger.info("Old RouteCodeMast Collection Exist");
                                    boolean sure= mongoDbDao.dropExistingCollection(actualRouteCodeMastCollection);
                                    if(sure){
                                        logger.info("Old RouteCodeMast Collection Drop");
                                    }else {
                                        logger.error("Database Connection Lost or System Problem");
                                    }
                                }
                                else {
                                    logger.info("OLD RouteCodeMast Collection Not Exist");
                                }
                                boolean finalStatus = mongoDbDao.renameCollection(DBName,tempRouteCodeMastCollection,actualRouteCodeMastCollection);
                                if (finalStatus){
                                    logger.info("Total RouteCodeMast Data Inserted Successfully to MongoDB is " +routeCodeCount);
                                    logger.info("New Temp Collection Name Rename To Actual RouteCodeMast Collection ");
                                    finishCheck=true;
                                }else {
                                    logger.info("Database Connection Lost or System Problem");
                                }
                            }
                        }
                        else {
                            logger.error("Failure RouteCodeMast Data save to mongoDB or connection lost " +i);
                        }
                    }
                    else {
                        logger.error("RouteCodeMast Data not Converted to Document");
                    }
                }
                else {
                    logger.info("RouteCodeMast Data Not found "+i);
                }
                skip=skip+batch;
            }
            return finishCheck;
        }
        catch (Exception e)
        {
            logger.error("RouteCodeMast Data Insertion Failed or Connection Lost " + e.getMessage(),e);
            return false;
        }
    }

    public List<RouteCodeMastDocument> getMongoDBRouteCode(List<RouteCodeMast> routeCodeMastList,String pharmacyID){
        List<RouteCodeMastDocument> routeCodeMastDocuments = new ArrayList<>();
        try {
            for (RouteCodeMast routeCodeMast:routeCodeMastList){
                RouteCodeMastDocument routeCodeMastDocument = new RouteCodeMastDocument();
                routeCodeMastDocument.setRouteCode(routeCodeMast.getRouteCode());
                routeCodeMastDocument.setDescription(routeCodeMast.getDescription());
                routeCodeMastDocument.setSystemicRouteIndicator(routeCodeMast.getSystemicRouteIndicator());
                routeCodeMastDocument.setDescriptionF(routeCodeMast.getDescriptionF());
                routeCodeMastDocument.setInsertTime(Constant.getInsertTime());
                routeCodeMastDocument.setPharmacyId(pharmacyID);
                routeCodeMastDocuments.add(routeCodeMastDocument);
            }
            logger.info("RouteCodeMast Record Converted to Document Successfully.....");
        }
        catch (Exception e){
            logger.error("RouteCodeMast Data not Converted to Document...." + e.getMessage(),e);
        }
        return routeCodeMastDocuments;

    }

}

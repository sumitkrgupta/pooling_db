package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.DrugMixDocument;
import com.PoolingAPI.Model.DrgMix;
import com.PoolingAPI.Repository.DrugMixDao;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class DrugMixDaoImpl implements DrugMixDao {

    private static final Logger logger = LoggerFactory.getLogger(DrugMixDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("jdbcMMPharmacy")
    private JdbcTemplate jdbcTemplate;


    //send Data to mongoDb
    @Override
    public boolean sendDrugsMixDataToMongo(List<DrugMixDocument> DrugMixDocument,String tempDrugMixCollection){
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,tempDrugMixCollection);
            for (DrugMixDocument drugMixDocument : DrugMixDocument) {
                bulkOperations.insert(drugMixDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            logger.info("DrugsMix Data Saved to Mongo DB Successfully");
            return true;
        }
        catch (MongoBulkWriteException e){
            logger.error("Failure to save DrugsMix Data to mongoDB or Connection Lost " +e.getMessage(),e);
            return false;
        }
    }

    @Override
    public Integer getTotalCount() {
        Integer drugMixCount = 0;
        try {
            final String query = "Select count(*) from DrgMix";
            drugMixCount = jdbcTemplate.queryForObject(query,Integer.class);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while Count data " + e.getMessage(),e);
        }
        return drugMixCount;
    }

    @Override
    public List<DrgMix> getDrugsMixData(int skip, int batch) {
        List<DrgMix> drgMixList = null;
        try {
            final String query = "Select ID,Description,DrgFormId from DrgMix " +
                    "Order By ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            drgMixList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(DrgMix.class),skip,batch);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving data " + e.getMessage(),e);
        }
        return drgMixList;
    }
}

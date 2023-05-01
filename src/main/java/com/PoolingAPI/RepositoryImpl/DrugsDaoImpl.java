package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.DrugDocument;
import com.PoolingAPI.Model.Drg;
import com.PoolingAPI.Repository.DrugsDao;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
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
public class DrugsDaoImpl implements DrugsDao {

    private static final Logger logger = LoggerFactory.getLogger(DrugsDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongoClient;
    @Autowired
    @Qualifier("jdbcMMPharmacy")
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean sendDrugsDataToMongo(List<DrugDocument> drugDocuments,String drugTempCollectionName){
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,drugTempCollectionName);
            for(DrugDocument drugDocument : drugDocuments) {
                bulkOperations.insert(drugDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            logger.info("Drugs Data Saved to Mongo DB Successfully");
            return true;
        }
        catch (MongoBulkWriteException e){
            logger.error("Failure to save Drugs Data to mongoDB " +e.getMessage(),e);
            return false;
        }
    }

    @Override
    public List<Drg> getDrugsDataInChunk(boolean drugPackActive,int skip, int batch) {
        List<Drg> drgList = null;
        try {
            final String query = "select d.ID as drgId,d.DIN,d.BrandName,d.GenericName,d.Strength," +
                    "d.Active as drgActive ,d.EquivTo,d.BrandGenericType,d.DefaultSig," +
                    "d.DrgFormId,d.ShapeId,d.Form as drugForm,dp.ID as drgPackID,dp.Active as drgPackActive,dp.PackSize " +
                    "from DrgPack dp left join Drg d on dp.DrgID = d.ID where dp.Active = ?" +
                    " Order By d.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            drgList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Drg.class),drugPackActive,skip,batch);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return drgList;
    }

    @Override
    public Integer getTotalCount(boolean drugPackActive){
        Integer drgCount = 0;
        try {
            final String query = "select count(*) from Drg  left join DrgPack on " +
                    "DrgPack.DrgID = Drg.ID where DrgPack.Active = ?";
            drgCount = jdbcTemplate.queryForObject(query,Integer.class,drugPackActive);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return drgCount;
    }
}

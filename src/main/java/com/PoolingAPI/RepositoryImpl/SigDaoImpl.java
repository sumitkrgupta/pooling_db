package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.SigDocument;
import com.PoolingAPI.Model.Sig;
import com.PoolingAPI.Repository.SigDao;
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
public class SigDaoImpl implements SigDao {

    private static final Logger logger = LoggerFactory.getLogger(SigDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    @Qualifier("jdbcMMPharmacy")
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean sendSigDataToMongo(List<SigDocument> sigDocuments,String collectionName){
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,collectionName);
            for (SigDocument sigDocument : sigDocuments) {
                bulkOperations.insert(sigDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            logger.info("Sig Data Saved to Mongo DB Successfully");
            return true;
        }
        catch (MongoException e){
            logger.error(e.getMessage(),e);
            logger.error("Failure to save Sig Data to mongoDB");
            return false;
        }
    }

    @Override
    public Integer getTotalCount(String lang) {
        Integer sigCount = 0;
        try {
            final String query = "Select count(*) from Sig where Language = ?";
            sigCount = jdbcTemplate.queryForObject(query,Integer.class,lang);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while Count data " + e.getMessage(),e);
        }
        return sigCount;
    }

    @Override
    public List<Sig> getSigData(String lang, int skip, int batch) {
        List<Sig> sigList = null;
        try {
            final String query = "Select Id,Token,Text,IsRouteOfAdmin from Sig " +
                    "where Language = ? Order By ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            sigList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Sig.class),lang,skip,batch);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving data " + e.getMessage(),e);
        }
        return sigList;
    }
}

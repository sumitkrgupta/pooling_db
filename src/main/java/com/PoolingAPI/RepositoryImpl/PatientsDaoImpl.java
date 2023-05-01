package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.PatientsDocument;
import com.PoolingAPI.Model.Pat;
import com.PoolingAPI.Repository.MongoDbDao;
import com.PoolingAPI.Repository.PatientsDao;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PatientsDaoImpl implements PatientsDao {

    private static final Logger logger = LoggerFactory.getLogger(PatientsDaoImpl.class);
    @Autowired
    @Qualifier("jdbcMMPharmacy")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${patient.pooling.LastID.filePath}")
    private String filePath;
    @Autowired
    private MongoDbDao mongoDbDao;

    @Override
    @Transactional(readOnly = true)
    public List<Pat> findPatientsData(String phoneType,int skip,int batch) {
        List<Pat> patList = null;
        try {
            final String query = "Select p.ID,p.LastName,p.FirstName,p.Birthday," +
                    "p.Sex,p.CreatedOn,p.LastChanged,p.AlternateLastName," +
                    "phn.Description,phn.Phone from Pat p left join PatPhone phn on p.ID=phn.PatID " +
                    "and phn.Description= ? Order By p.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            patList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Pat.class),phoneType,skip,batch);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving data " + e.getMessage(),e);
        }
        return patList;
    }

    @Override
    public List<Pat> findPatientsDataByGreaterThanLastId(String phoneType,int lastId) {
        List<Pat> patList = null;
        try {
            final String query = "Select p.ID,p.LastName,p.FirstName,p.Birthday," +
                    "p.Sex,p.CreatedOn,p.LastChanged,p.AlternateLastName," +
                    "phn.Description,phn.Phone from Pat p left join PatPhone phn on p.ID=phn.PatID " +
                    "and phn.Description= ? where p.ID > ? Order By p.ID";

            patList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Pat.class),phoneType,lastId);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving data " + e.getMessage(),e);
        }
        return patList;
    }

    @Override
    public boolean sendPatientsDataToMongo(List<PatientsDocument> patientsDocuments,String patientsCollection) throws IOException {
        int lastInsertID =0;
        String patientsLastInsertedKey = "patientLastInsertedId";
        String patientsDateKey = "patientTimeStamp";
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,patientsCollection);
            for (PatientsDocument patientsDocument:patientsDocuments) {
                lastInsertID = patientsDocument.getPat_id();
                bulkOperations.insert(patientsDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            mongoDbDao.writeLastInsertedId(lastInsertID,filePath,patientsLastInsertedKey,patientsDateKey);
            logger.info("patients Data Insert Successfully");
            return true;
        }
        catch (MongoBulkWriteException exception){
            mongoDbDao.writeLastInsertedId(lastInsertID,filePath,patientsLastInsertedKey,patientsDateKey);
            logger.error("Failure to send Patients Data to mongoDB " +exception.getMessage(),exception);
            return false;
        }
    }

    @Override
    public Integer getTotalCount(String phoneType) {
        Integer patCount = 0;
        try {
            final String query = "Select count(p.ID) from Pat p " +
                    "left join PatPhone phn on p.ID=phn.PatID and phn.Description=?";
            patCount = jdbcTemplate.queryForObject(query,Integer.class,phoneType);
        }
        catch (NullPointerException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return patCount;
    }

    @Override
    public Integer getTotalCountByLastID(String phoneType,int lastId) {
        Integer patCount = 0;
        try {
            final String query = "Select count(p.ID) from Pat p " +
                    "left join PatPhone phn on p.ID=phn.PatID and phn.Description=? where p.ID > ?";
            patCount = jdbcTemplate.queryForObject(query,Integer.class,phoneType,lastId);
        }
        catch (NullPointerException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return patCount;
    }
}


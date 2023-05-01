package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.DoctorDocument;
import com.PoolingAPI.Model.Doc;
import com.PoolingAPI.Repository.DoctorsDao;
import com.PoolingAPI.Repository.MongoDbDao;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
public class DoctorDaoImpl implements DoctorsDao {

    private static final Logger logger = LoggerFactory.getLogger(DoctorDaoImpl.class);
    @Autowired
    @Qualifier("jdbcMMPharmacy")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${doctor.pooling.LastID.filePath}")
    private String filePath;
    @Autowired
    private MongoDbDao mongoDbDao;

    @Override
    @Transactional(readOnly = true)
    public List<Doc> findDoctorsData(String phoneType,int skip,int batch){
        List<Doc> docList =null;
        try {
                final String query = "Select d.ID,d.LastName,d.FirstName,d.Salutation," +
                    "d.Designation,d.Specialty,d.Created," +
                    "d.Changed,phn.Description,d.DispensingRights," +
                    "d.Active,d.licence1,phn.Phone from Doc d left join DocPhone phn on d.ID=phn.DocID and" +
                    " phn.Description =? Order By d.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            docList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Doc.class),phoneType,skip,batch);
            logger.info("Doctor Data found");

        } catch (DataAccessException e) {
            logger.error("A problem occurred while retrieving my data " +e.getMessage(), e);
        }
        return docList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Doc> findDoctorDataByGreaterThanLastId(String phoneType,int lastId) {
        List<Doc> docList =null;
        try {
            final String query = "Select d.ID,d.LastName,d.FirstName,d.Salutation," +
                    "d.Designation,d.Specialty,d.Created," +
                    "d.Changed,phn.Description,d.DispensingRights," +
                    "d.Active,d.licence1,phn.Phone from Doc d left join DocPhone phn on d.ID=phn.DocID and" +
                    " phn.Description =? where d.ID > ? Order By d.ID";

            docList = jdbcTemplate.query(query,new BeanPropertyRowMapper<>(Doc.class),phoneType,lastId);
            logger.info("Doctor Data found");

        } catch (DataAccessException e) {
            logger.error("A problem occurred while retrieving data " +e.getMessage(), e);
        }
        return docList;
    }

    @Override
    public boolean sendDoctorsDataToMongo(List<DoctorDocument> doctorDocuments, String doctorCollection) throws IOException {
        int lastInsertID =0;
        String doctorLastInsertedKey = "DoctorLastInsertedId";
        String doctorDateKey = "DoctorTimeStamp";
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,doctorCollection);
            for (DoctorDocument doctorDocument : doctorDocuments) {
                lastInsertID = doctorDocument.getDoc_id();
                bulkOperations.insert(doctorDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            mongoDbDao.writeLastInsertedId(lastInsertID,filePath,doctorLastInsertedKey,doctorDateKey);
            logger.info("Doctors Data Save to mongoDB Successfully.....");
            return true;
        }
        catch (MongoBulkWriteException exception){
            mongoDbDao.writeLastInsertedId(lastInsertID,filePath,doctorLastInsertedKey,doctorDateKey);
            logger.error("Failure to save Doctors Data to mongoDB " +exception.getMessage(),exception);
            return false;
        }
    }

    @Override
    public Integer getTotalCount(String phoneType) {
        Integer docCount = 0;
        try {
            final String query = "Select count(d.ID) from Doc d left join DocPhone phn " +
                    "on d.ID=phn.DocID and phn.Description = ?";
            docCount = jdbcTemplate.queryForObject(query,Integer.class,phoneType);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return docCount;
    }

    @Override
    public Integer getTotalCountByLastID(String phoneType,int lastId) {
        Integer docCount = 0;
        try {
            final String query = "Select count(d.ID) from Doc d left join DocPhone phn " +
                    "on d.ID=phn.DocID and phn.Description = ? where d.ID > ?";
            docCount = jdbcTemplate.queryForObject(query,Integer.class,phoneType,lastId);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while retrieving my data " + e.getMessage(),e);
        }
        return docCount;
    }

}

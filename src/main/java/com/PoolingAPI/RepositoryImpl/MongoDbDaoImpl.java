package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Repository.MongoDbDao;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.MongoException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MongoDbDaoImpl implements MongoDbDao {

    private static final Logger logger = LoggerFactory.getLogger(MongoDbDaoImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoClient mongoClient;

    @Override
    public boolean collectionExist(String DBName, String ExistCollection) {
        return mongoClient.getDatabase(DBName).listCollectionNames()
                .into(new ArrayList<String>()).contains(ExistCollection);
    }

    @Override
    public boolean renameCollection(String DBName,String ExistCollection,String renameCollectionTo) {
        try {
            MongoNamespace mongoNamespace = new MongoNamespace(DBName,renameCollectionTo);
            mongoTemplate.getCollection(ExistCollection).renameCollection(mongoNamespace);
            return true;

        }catch (MongoException e){
            logger.error("Collection not Rename" +e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean dropExistingCollection(String DropRenameCollection) {
        try {
            mongoTemplate.dropCollection(DropRenameCollection);
            return true;
        }
        catch (MongoException e){
            logger.error("Drop Collection Failed" +e.getMessage(),e);
            return false;
        }
    }
    @Override
    public void writeLastInsertedId(int lastID,String path,String lastInsertedKey,String dateKey) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer(new DefaultPrettyPrinter());
        Map<String,Object> mapLastId = new HashMap<>();
        mapLastId.put(lastInsertedKey,lastID);
        mapLastId.put(dateKey, LocalDateTime.now().toString());
        objectWriter.writeValue(new File(path),mapLastId);
    }
}

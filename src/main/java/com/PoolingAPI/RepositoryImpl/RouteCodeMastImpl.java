package com.PoolingAPI.RepositoryImpl;

import com.PoolingAPI.Document.RouteCodeMastDocument;
import com.PoolingAPI.Model.RouteCodeMast;
import com.PoolingAPI.Repository.RouteCodeMastDao;
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
public class RouteCodeMastImpl implements RouteCodeMastDao {

    private static final Logger logger = LoggerFactory.getLogger(RouteCodeMastImpl.class);
    @Autowired
    @Qualifier("jdbcFDB")
    private JdbcTemplate jdbcTemplateFdb;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<RouteCodeMast> findRouteCodeMast(int skip,int batch) {
        List<RouteCodeMast> routeCodeMastList =null;
        try {
            final String query = "select r.RouteCode,r.Description,r.SystemicRouteIndicator," +
                    "r.DescriptionF from RouteCodeMast r ORDER BY r.RouteCode OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            routeCodeMastList = jdbcTemplateFdb.query(query,new BeanPropertyRowMapper<>(RouteCodeMast.class),skip,batch);
            logger.info("RouteCodeMast Data found");

        } catch (DataAccessException e) {
            logger.error("A problem occurred while retrieving data " +e.getMessage(), e);
        }
        return routeCodeMastList;
    }

    @Override
    public Integer getTotalCount() {
        Integer routeCodeCount = 0;
        try {
            final String query = "Select count(RouteCode) from RouteCodeMast";
            routeCodeCount = jdbcTemplateFdb.queryForObject(query,Integer.class);
        }
        catch (DataAccessException e){
            logger.error("A problem occurred while Count data " + e.getMessage(),e);
        }
        return routeCodeCount;
    }

    @Override
    public boolean sendRouteDataToMongo(List<RouteCodeMastDocument> routeCodeMastDocuments, String routeCodeCollection) {
        try{
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED,routeCodeCollection);
            for (RouteCodeMastDocument routeCodeMastDocument : routeCodeMastDocuments) {
                bulkOperations.insert(routeCodeMastDocument);
            }
            BulkWriteResult result = bulkOperations.execute();
            logger.info("RouteCode Data Saved to Mongo DB Successfully");
            return true;
        }
        catch (MongoException e){
            logger.error(e.getMessage(),e);
            logger.error("Failure to save RouteCode Data to mongoDB");
            return false;
        }
    }
}

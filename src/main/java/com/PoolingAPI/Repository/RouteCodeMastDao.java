package com.PoolingAPI.Repository;

import com.PoolingAPI.Document.RouteCodeMastDocument;
import com.PoolingAPI.Model.RouteCodeMast;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Repository
public interface RouteCodeMastDao {

    @Transactional(readOnly = true)
    List<RouteCodeMast> findRouteCodeMast(int skip,int batch) throws SQLException;
    @Transactional(readOnly = true)
    Integer getTotalCount();
    @Transactional
    boolean sendRouteDataToMongo(List<RouteCodeMastDocument> routeCodeMastDocuments, String routeCodeCollection);
}

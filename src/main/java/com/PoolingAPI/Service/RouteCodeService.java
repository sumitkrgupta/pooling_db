package com.PoolingAPI.Service;

import com.PoolingAPI.Document.RouteCodeMastDocument;
import com.PoolingAPI.Model.RouteCodeMast;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RouteCodeService {

    boolean routeCodeMastScheduler();
    List<RouteCodeMastDocument> getMongoDBRouteCode(List<RouteCodeMast> routeCodeMastList, String pharmacyID);
}

package com.PoolingAPI.Service;

import com.PoolingAPI.Document.DrugDocument;
import com.PoolingAPI.Model.Drg;
import com.PoolingAPI.Model.RequestFormat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DrugService {

    Map<String,Object> getDrugsDataAndSaveToMongo(RequestFormat requestFormats);
    List<DrugDocument> getMongoDBDrugsData(List<Drg> drugs, String pharmacyID);
    boolean drugsScheduler();
}

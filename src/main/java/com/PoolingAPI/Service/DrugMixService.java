package com.PoolingAPI.Service;

import com.PoolingAPI.Document.DrugMixDocument;
import com.PoolingAPI.Model.DrgMix;
import com.PoolingAPI.Model.RequestFormat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DrugMixService {

    Map<String,Object> getDrugsMixDataAndSaveToMongo(RequestFormat requestFormats);
    List<DrugMixDocument> getMongoDBDrugMixData(List<DrgMix> drgMixes, String pharmacyID);
    boolean drugMixScheduler();
}

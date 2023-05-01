package com.PoolingAPI.Service;

import com.PoolingAPI.Document.SigDocument;
import com.PoolingAPI.Model.RequestFormat;
import com.PoolingAPI.Model.Sig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface SigService {

    Map<String,Object> getSigDataAndSaveToMongo(RequestFormat requestFormats);
    List<SigDocument> getMongoDBSigData(List<Sig> sigs, String pharmacyID);
    boolean sigScheduler();
}

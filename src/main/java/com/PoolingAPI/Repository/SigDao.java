package com.PoolingAPI.Repository;


import com.PoolingAPI.Document.SigDocument;
import com.PoolingAPI.Model.Sig;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SigDao {

    @Transactional
    boolean sendSigDataToMongo(List<SigDocument> sigDocuments,String tempSigCollection);
    @Transactional
    Integer getTotalCount(String lang);
    @Transactional
    List<Sig> getSigData(String lang, int skip, int batch);
}

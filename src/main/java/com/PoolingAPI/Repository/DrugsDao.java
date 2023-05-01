package com.PoolingAPI.Repository;


import com.PoolingAPI.Document.DrugDocument;
import com.PoolingAPI.Model.Drg;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DrugsDao {

    @Transactional
    boolean sendDrugsDataToMongo(List<DrugDocument> drugDocuments,String drugTempCollectionName);
    @Transactional(readOnly = true)
    Integer getTotalCount(boolean drugPackActive);
    @Transactional(readOnly = true)
    List<Drg> getDrugsDataInChunk(boolean drugPackActive,int skip, int batch);

}

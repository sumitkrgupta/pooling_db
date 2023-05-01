package com.PoolingAPI.Repository;


import com.PoolingAPI.Document.DrugMixDocument;
import com.PoolingAPI.Model.DrgMix;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DrugMixDao {

    @Transactional
    boolean sendDrugsMixDataToMongo(List<DrugMixDocument> DrugMixDocument,String tempDrugMixCollection);
    @Transactional
    Integer getTotalCount();
    @Transactional
    List<DrgMix> getDrugsMixData(int skip, int batch);
}

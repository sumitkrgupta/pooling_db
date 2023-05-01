package com.PoolingAPI.Repository;

import com.PoolingAPI.Document.PatientsDocument;
import com.PoolingAPI.Model.Pat;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface PatientsDao {

    @Transactional(readOnly = true)
    List<Pat> findPatientsData(String phoneType,int skip,int batch) throws SQLException;
    @Transactional
    List<Pat> findPatientsDataByGreaterThanLastId(String phoneType,int lastId);
    @Transactional
    boolean sendPatientsDataToMongo(List<PatientsDocument> patientsDocuments,String patientsCollection) throws IOException;
    @Transactional(readOnly = true)
    Integer getTotalCount(String phoneType);
    @Transactional
    Integer getTotalCountByLastID(String phoneType,int lastId);

}

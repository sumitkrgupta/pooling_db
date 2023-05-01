package com.PoolingAPI.Repository;


import com.PoolingAPI.Document.DoctorDocument;
import com.PoolingAPI.Model.Doc;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface DoctorsDao {

    /**
     * @param phoneType : doctor phone type
     * @param skip : skip the data
     * @param batch : fetch data from batch wise
     */
    @Transactional(readOnly = true)
    List<Doc> findDoctorsData(String phoneType,int skip,int batch) throws SQLException;

    @Transactional(readOnly = true)
    List<Doc> findDoctorDataByGreaterThanLastId(String phoneType,int lastId) throws SQLException;
    @Transactional(readOnly = true)
    Integer getTotalCountByLastID(String phoneType,int lastId);
    /**
     * @param doctorDocuments : doctor list to save data to mongo DB
     * @param doctorCollection : doctor temp collection name
     */
    @Transactional
    boolean sendDoctorsDataToMongo(List<DoctorDocument> doctorDocuments,String doctorCollection) throws IOException;
    /**
     * @param phoneType : doctor phone type
     */
    @Transactional(readOnly = true)
    Integer getTotalCount(String phoneType);

}


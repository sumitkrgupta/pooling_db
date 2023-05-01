package com.PoolingAPI.Service;

import com.PoolingAPI.Document.DoctorDocument;
import com.PoolingAPI.Model.Doc;
import com.PoolingAPI.Model.RequestFormat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DoctorService {

    Map<String,Object> getDoctorsDataAndSaveToMongo(RequestFormat requestFormats);
    List<DoctorDocument> getMongoDBDoctorsData(List<Doc> docs, String pharmacyID,Map<Integer,Integer> filterDuplicate);
    boolean doctorScheduling();
    boolean doctorSchedulingByLastId();
    Integer getLastInsertedIdFromFile(String path);
}

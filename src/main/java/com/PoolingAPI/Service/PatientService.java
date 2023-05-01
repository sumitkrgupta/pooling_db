package com.PoolingAPI.Service;

import com.PoolingAPI.Document.PatientsDocument;
import com.PoolingAPI.Model.Pat;
import com.PoolingAPI.Model.RequestFormat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PatientService {

    Map<String,Object> getPatientsDataAndSaveToMongo(RequestFormat requestFormats);
    List<PatientsDocument> getMongoDBPatientsData(List<Pat> pats, String pharmacyID, Map<Integer,Integer> filterDuplicate);
    boolean patientScheduling();
    boolean patientSchedulingById();
    Integer getLastInsertedIdFromFile(String path);
}

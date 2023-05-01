package com.PoolingAPI.Controller;


import com.PoolingAPI.Model.RequestFormat;
import com.PoolingAPI.Service.*;
import com.PoolingAPI.Service.Serviceimpl.DrugMixServiceImpl;
import com.PoolingAPI.Service.Serviceimpl.SigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/pass")
public class PoolingAPIController {

    private static final Logger logger = LoggerFactory.getLogger(PoolingAPIController.class);
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DrugService drugService;
    @Autowired
    private DrugMixService drugMixService;
    @Autowired
    private SigService sigService;

    @RequestMapping(value = "/patients", method = RequestMethod.POST)
    Map<String, Object> requestPatients(@RequestBody @Valid RequestFormat requestFormats) {
        logger.info("RequestBody for patients Data is valid");
        Map<String, Object> response = patientService.getPatientsDataAndSaveToMongo(requestFormats);
        logger.info("End Pooling API Service");
        return response;
    }

    @RequestMapping(value = "/doctors", method = RequestMethod.POST)
    Map<String, Object> requestDoctors(@RequestBody @Valid RequestFormat requestFormats) {
        logger.info("RequestBody doctors Data is valid");
        Map<String, Object> response  = doctorService.getDoctorsDataAndSaveToMongo(requestFormats);
        logger.info("End Pooling API Service");
        return response;
    }

    @RequestMapping(value = "/drugs", method = RequestMethod.POST)
    Map<String, Object> requestDrugs(@RequestBody @Valid RequestFormat requestFormats) {
        logger.info("RequestBody drugs Data is valid");
        Map<String, Object> response  = drugService.getDrugsDataAndSaveToMongo(requestFormats);
        logger.info("End Pooling API Service");
        return response;
    }

    @RequestMapping(value = "/drugsmix", method = RequestMethod.POST)
    Map<String, Object> requestDrugsMix(@RequestBody @Valid RequestFormat requestFormats) {
        logger.info("RequestBody drugsMix Data is valid");
        Map<String, Object> response  = drugMixService.getDrugsMixDataAndSaveToMongo(requestFormats);
        logger.info("End Pooling API Service");
        return response;
    }

    @RequestMapping(value = "/sig", method = RequestMethod.POST)
    Map<String, Object> requestSig(@RequestBody @Valid RequestFormat requestFormats) {
        logger.info("RequestBody Sig Data is valid");
        Map<String, Object> response  = sigService.getSigDataAndSaveToMongo(requestFormats);
        logger.info("End Pooling API Service");
        return response;
    }
}

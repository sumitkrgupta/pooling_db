package com.PoolingAPI.Util;



import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Constant {

    public static final Integer successStatusCode = 200;
    public static final Integer failureStatusCode = 199;
    public static final String successStatus = "Success";
    public static final String failedStatus = "Failed";


    public static Timestamp getInsertTime(){
        Date currentDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = format.format(currentDate);
        return Timestamp.valueOf(currentTime);
    }

//    public void transactionHandleInMongo(){
//        ClientSession session = mongoClient.startSession();
//        session.startTransaction();
//        session.commitTransaction();
//        session.close();
//        session.abortTransaction();
//    }

}

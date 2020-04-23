package com.zcyk.service;


public interface LogService {

    void put(String conment, String methodName, String module, String description);
}

package com.hyll.godtools.service;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.Page;

import com.hyll.godtools.pojo.TransportEntity;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TranspotrService {

    Map PageQuery(int pageNo, int pageNum, int pageSize);

    Map<Integer, List<TransportEntity>> compareByExcel(List<TransportEntity> List);

    int delByList(List<String> list);

    int delByBatchID(String BatchID);

    void inserSqlByEccal(List<TransportEntity> list,String fileMD5);

    Boolean SeleteByFileMD5(String fileMD5);

    Boolean checkFile(MultipartFile file);

    Map<String,Object> getResultsMap(Page<T> transpotrPage);

    void setJedisTransoportEntity(String sequenceId,Map<Integer,List<TransportEntity>> listMap);

    Map<Integer,List<TransportEntity>> getJedisTransportEntity(String sequenceId,Integer pageNum,Integer pageSize);

    long getJedisTransportEntityTotal(String sequenceId);

    void initCache();
}

package com.hyll.godtools.service;

import cn.hutool.json.JSONObject;
import com.github.pagehelper.Page;
import com.hyll.godtools.pojo.Transport;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TranspotrService {

    Page PageQuery(int pageNo, int pageNum, int pageSize);

//    int upSqlByEccal(List<Transport> List);

    Map<Integer, List<Transport>> compareByExcel(List<Transport> List);

    int delByList(List<String> list);

    int delByBatchID(String BatchID);

    public int inserSqlByEccal(List<Transport> list) throws IOException;

}

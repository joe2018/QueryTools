package com.hyll.godtools.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hyll.godtools.mapper.TableTypeMapper;
import com.hyll.godtools.mapper.TranspotrMapper;
import com.hyll.godtools.pojo.TableType;

import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Log4j2
public class TranspotrServiceImpl implements TranspotrService {

    @Autowired
    TranspotrMapper transpotrMapper;

    @Autowired
    TableTypeMapper tableTypeMapper;

    /**
     * 初始化线程池，同时执行10个线程
     */


    @Override
    public int inserSqlByEccal(List<TransportEntity> list) {
            TableType tableType = new TableType();
            //参数1为终端ID
            //参数2为数据中心ID
            ExecutorService executor = ThreadUtil.newExecutor(10);
            Snowflake snowflake = IdUtil.createSnowflake(1, 1);
            long id = snowflake.nextId();
            String batchId = String.valueOf(id);
            Date date = DateTime.now();
            if(!batchId.isEmpty()){
                CopyOnWriteArrayList<TransportEntity> tempList = new CopyOnWriteArrayList<>(list);
                tempList.forEach(item ->{
                    executor.execute(() -> {
                        if(transpotrMapper.select(item).size() == 0){
                            item.setLoading_date(item.getLoading_time());
                            item.setOccurrence_time(date);
                            item.setBatch_number(batchId);
                            transpotrMapper.insert(item);
                        }
                    });
                });
                executor.shutdown();
                tableType.setBatch_number(batchId);
                tableType.setBatch_time(date);
                tableTypeMapper.insertSelective(tableType);
            }
            return 0;
    }

    @Override
    public Page PageQuery(int pageNum, int pageSize,int typeId) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //用page类型或者pageInfo类型目的就是获取总页数以及总条数
        if (typeId == 1){
            return (Page<TransportEntity>) transpotrMapper.selectAll();
        }else {
            return (Page<TableType>) tableTypeMapper.selectAll();
        }
    }

    @Override
    public Map<Integer, List<TransportEntity>> compareByExcel(List<TransportEntity> list){
        Map<Integer, List<TransportEntity>> resultMap = new HashMap<>();
        for (int index = 0; index < list.size(); index++) {
            TransportEntity item = list.get(index);
            TransportEntity transport = new TransportEntity();
            transport.setTracking_number(item.getTracking_number());
            transport.setLoading_date(item.getLoading_date());
            List<TransportEntity> sourceData = transpotrMapper.select(transport);
            List<TransportEntity> dataList = new ArrayList<>();
            if (!sourceData.isEmpty()){
                dataList.add(sourceData.get(0));
            }
            dataList.add(item);
            resultMap.put(index, dataList);
        }
        return resultMap;
    }

    @Override
    public int delByList(List<String> list) {
        list.forEach(item ->{
            transpotrMapper.deleteByID(item);
        });
        return 0;
    }

    @Override
    public int delByBatchID(String BatchID) {
        transpotrMapper.deleteByBatchID(BatchID);
        tableTypeMapper.deleteByBatchID(BatchID);
        return 0;
    }


}

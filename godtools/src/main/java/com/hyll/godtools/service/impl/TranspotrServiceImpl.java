package com.hyll.godtools.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hyll.godtools.pojo.chche.CacheSequenceId;
import com.hyll.godtools.util.JedisUtil;
import com.hyll.godtools.config.SequenceId;
import com.hyll.godtools.mapper.TableTypeMapper;
import com.hyll.godtools.mapper.TranspotrMapper;
import com.hyll.godtools.pojo.Constant;
import com.hyll.godtools.pojo.TableType;

import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import com.hyll.godtools.util.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;



@Service
@Log4j2
public class TranspotrServiceImpl implements TranspotrService {

    @Autowired
    TranspotrMapper transpotrMapper;

    @Autowired
    TableTypeMapper tableTypeMapper;

    @Autowired
    private SequenceId sequenceId;
    //订单号缓存
    @CreateCache(name = Constant.ORDER_NUMBER)
    private Cache<String,String>  cacheOrderNumber;
    //车牌号+装货时间缓存
    @CreateCache(name = Constant.PLATE_NO)
    private Cache<String,String> cachePlateNo;

    @CreateCache(name = Constant.SEQUENCE_ID)
    private Cache<String,String> cacheSequenceId;
    @Autowired
    private JedisUtil jedisUtil;
    /**
     * 初始化线程池，同时执行10个线程
     */




    /*@Override
    @Transactional
    public void inserSqlByEccal(List<TransportEntity> list,String fileMD5) {
            TableType tableType = new TableType();
            //参数1为终端ID
            //参数2为数据中心ID
            ExecutorService executor = ThreadUtil.newExecutor(50);
            Snowflake snowflake = IdUtil.createSnowflake(1, 1);
            String batchId = Convert.toStr(snowflake.nextId());
            Date date = DateTime.now();
            if(!batchId.isEmpty()){
                list.forEach(item ->{
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
                tableType.setFile_md5(fileMD5);
                tableTypeMapper.insertSelective(tableType);
            }
    }*/


    @Override
    @Transactional
    public void inserSqlByEccal(List<TransportEntity> list,String fileMD5) {
        List<TransportEntity> entities = new ArrayList<>();
        try {
            TableType tableType = new TableType();
            //参数1为终端ID
            //参数2为数据中心ID
            Snowflake snowflake = IdUtil.createSnowflake(1, 1);
            String batchId = Convert.toStr(snowflake.nextId());
            Date date = DateTime.now();
            int size = list.size();
            List<List<TransportEntity>> threadList = new ArrayList<>();
            if(!batchId.isEmpty()){
                for(int i = 0;i<size;i++){
                    if(StrUtil.isEmpty(cacheOrderNumber.get(list.get(i).getOrder_number()))){
                        list.get(i).setBatch_number(batchId);
                        list.get(i).setLoading_date(list.get(i).getLoading_time());
                        list.get(i).setOccurrence_time(date);
                        entities.add(list.get(i));
                        if((entities.size()==1000||i==size-1)&&entities.size()>0){
                            threadList.add(entities);
                            entities = new ArrayList<>();

                        };
                    }
                }
            }

            ExecutorService executorService = Executors.newFixedThreadPool(15);
            //同步多线程入库
            CompletionService<String> completionService = ThreadUtil.newCompletionService(executorService);
            threadList.stream().forEach(f->{
                completionService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        try {
                            transpotrMapper.batchInsertTranspotrMapper(f);
                            pushCache(f);
                        }catch (Exception e){
                            e.printStackTrace();
                            removeCache(f);
                        }
                        return "成功："+f.size();
                    }
                });
            });
            threadList.stream().forEach(f->{
                try {
                    completionService.take().get();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            executorService.shutdown();
            tableType.setBatch_number(batchId);
                tableType.setBatch_time(date);
                tableType.setFile_md5(fileMD5);
                tableTypeMapper.insertSelective(tableType);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Map PageQuery(int pageNum, int pageSize, int typeId) {
//        Jedis jedis = RedisDS.create().getJedis();
        Jedis jedis = jedisUtil.getJedis();
        Page entityPage;
        String Key;
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //用page类型或者pageInfo类型目的就是获取总页数以及总条数
        if (typeId == 1){
            entityPage = (Page<TransportEntity>)transpotrMapper.selectAll();
            Key = "1—"+pageNum+"-"+pageSize;
        }else {
            entityPage = (Page<TableType>)tableTypeMapper.selectAll();
            Key = "2—"+pageNum+"-"+pageSize;
        }
        jedis.del(Key);
        JSON jsonObject = JSONUtil.parse(getResultsMap(entityPage));
        jedis.setex(Key,5, jsonObject.toString());
        return getResultsMap(entityPage);
    }

    @Override
    public Map<Integer, List<TransportEntity>> compareByExcel(List<TransportEntity> list){
        Map<Integer, List<TransportEntity>> resultMap = new HashMap<>();
        list.stream().forEach(item ->{
            List<TransportEntity> dataList = new ArrayList<>();
            String entity = cachePlateNo.get(item.getLicense_plate() + DateUtils.formatDate(item.getLoading_time()));
            if(StrUtil.isNotEmpty(entity)){
                TransportEntity transportEntity = JSONUtil.toBean(entity, TransportEntity.class);
                dataList.add(transportEntity);
                dataList.add(item);
                resultMap.put(resultMap.size(), dataList);
            }
        });
        return resultMap;
    }

    @Override
    @Transactional
    public int delByList(List<String> list) {
        List<TransportEntity> entityList = transpotrMapper.findListTransportEntityById(list);
        removeCache(entityList);
        //清除缓存信息记录
        transpotrMapper.deleteListTransportEntityById(list);
        /*list.forEach(item ->{
            transpotrMapper.deleteByID(item);
        });*/
        return 0;
    }

    @Override
    @Transactional
    public int delByBatchID(String BatchID) {
        List<TransportEntity> entityList = transpotrMapper.findListTranportEntityByBatchNumber(BatchID);
        removeCache(entityList);
        transpotrMapper.deleteByBatchID(BatchID);
        tableTypeMapper.deleteByBatchID(BatchID);
        return 0;
    }



    @Override
    public Boolean SeleteByFileMD5(String fileMD5){
        return tableTypeMapper.SeleteByFileMD5(fileMD5).size() > 0;
    }

    @Override
    public Boolean checkFile(MultipartFile file){
        if (file.isEmpty()) {
            return false;
        }
        String fileName = FileNameUtil.extName(file.getOriginalFilename());
        assert fileName != null;
        return fileName.equals("xlsx") | fileName.equals("xls");
    }

    /**
     * page转map
     * @param transpotrPage 分页查询结果
     * @return 数据map
     */
    @Override
    public Map<String,Object> getResultsMap(Page<T> transpotrPage) {
        Map <String,Object> resultsMap = new HashMap<>();
        HashMap<Object, Object> colorMap = Convert.convert(new TypeReference<HashMap<Object, Object>>() {}, transpotrPage);
        resultsMap.put("info",colorMap);
        resultsMap.put("content", Convert.toList(transpotrPage.toArray()));
        return resultsMap;
    }



    /**
     * 初始化缓存信息
     */
//    @PostConstruct
    @Override
    public void initCache(){
        try {
            Integer pageSize = 50000;
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            long total = transpotrMapper.findCountTranportEntity();
            long l = total / pageSize+1;
            //同步多线程入库
            CompletionService<String> completionService = ThreadUtil.newCompletionService(executorService);
            for(int i =1;i<=l;i++){
                int finalI = i;
                completionService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        List<TransportEntity> cache = transpotrMapper.findListTransportEntity((finalI -1)*pageSize,pageSize);
                        pushCache(cache);
                        return null;
                    }
                });
            }
            for(int i = 1;i<l;i++){
                completionService.take().get();
            }
            executorService.shutdown();

            log.info("初始化缓存成功");
        }catch (Exception e){
            e.printStackTrace();
            log.error("初始化缓存失败");
        }
    }

    /**
     * 获取缓存的比对数据
     * @param sequenceId  比对时，返回给前端的sequenceId
     * @param pageNum   第几页开始
     * @param pageSize  获取多少条数据
     * @return
     */
    @Override
    public Map<Integer, List<TransportEntity>>  getJedisTransportEntity(String sequenceId,Integer pageNum,Integer pageSize){
        Jedis jedis = jedisUtil.getJedis();
        List<String> lrange = jedis.lrange(sequenceId, (pageNum - 1) * pageSize, (pageNum - 1) * pageSize+pageSize-1);
        Map<Integer,List<TransportEntity>> map = new HashMap<>();
        lrange.stream().forEach(f->{
            List<TransportEntity> entities = JSONUtil.toList(JSONUtil.parseArray(f), TransportEntity.class);
            map.put(map.size()+1,entities);
        });
        return map;
    }

    /**
     * 返回总条数
     * @param sequenceId 操作id
     * @return
     */
    @Override
    public long getJedisTransportEntityTotal(String sequenceId){
        Jedis jedis = jedisUtil.getJedis();
        return jedis.llen(sequenceId);
    }

    /**
     * 设置缓存比对数据
     * @param sequenceId    缓存数据的Id
     * @param integerListMap
     */
    @Override
    public void setJedisTransoportEntity(String sequenceId, Map<Integer, List<TransportEntity>> integerListMap){
        try {
            Jedis jedis = jedisUtil.getJedis();
            Set<Integer> integers = integerListMap.keySet();
            integers.stream().forEach(f->{
                jedis.rpush(sequenceId,JSONUtil.parse(integerListMap.get(f)).toString());
            });
            //记录创建时间
            CacheSequenceId c = new CacheSequenceId();
            c.setSequenceId(sequenceId);
            c.setCreateTime(DateUtil.now());
            cacheSequenceId.put(sequenceId,JSONUtil.toJsonStr(c));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 传递需要清除缓存的集合
     * @param entityList
     */
    private void removeCache(List<TransportEntity> entityList){
        if(entityList.size()>0){
            //清除缓存信息记录
            Set<String> orderNumberList = entityList.stream().map(TransportEntity::getOrder_number).collect(Collectors.toSet());
            Set<String> plateNo = entityList.stream().map(t->t.getLicense_plate()+DateUtils.formatDate(t.getLoading_time())).collect(Collectors.toSet());
            cacheOrderNumber.removeAll(orderNumberList);
            cachePlateNo.removeAll(plateNo);
        }
    }

    /**
     * 传递需要新增的缓存集合
     * @param entityList
     */
    private void pushCache(List<TransportEntity> entityList){
        if(entityList.size()>0){
            Map<String,String> collect = entityList.stream().collect(Collectors.toMap(TransportEntity::getOrder_number, TransportEntity::getOrder_number));
            cacheOrderNumber.putAll(collect);
            collect = entityList.stream().collect(Collectors.toMap(t->t.getLicense_plate()+DateUtils.formatDate(t.getLoading_time()),t->JSONUtil.toJsonStr(t),(t1,t2)->t1));
            cachePlateNo.putAll(collect);
        }
    }

}

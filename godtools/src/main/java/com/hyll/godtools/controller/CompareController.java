package com.hyll.godtools.controller;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.hyll.godtools.config.ResultCode;
import com.hyll.godtools.config.SequenceId;
import com.hyll.godtools.pojo.Result;
import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import com.hyll.godtools.util.ReadExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Api(tags = "compareApi")
@RequestMapping("/compare")
@Log4j2
@CrossOrigin
public class CompareController {

    @Autowired
    private TranspotrService transpotrService;
    @Autowired
    private SequenceId sequenceId;

    /**
     * 上传需要比对的表进行比对，输出比对结果，若车牌号及装货时间存在相同则视为异常，同时输出源数据与导入的数据，若无则输出导入数据
     * @param file 需要进行对比的数据
     * @return 对比结果Json格式
     */
    @ResponseBody
    @ApiOperation(value="上传对比文件", notes="上传对比文件与数据库中数据进行比对", produces="application/json")
    @ApiImplicitParam(name = "file", value = "比对文件", paramType = "query", required = true, dataType = "file")
    @RequestMapping(value = "/upfile", method = RequestMethod.POST)
    public Result compareData(@RequestParam(value = "file",required = false) MultipartFile file) {
        if (!transpotrService.checkFile(file)){
            return Result.failure(ResultCode.FILE_WRITE_FAILURE);
        }
        try {
//            List<TransportEntity> transportEntityList = ReadExcel.readExcel(file);
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
           /* long start = System.currentTimeMillis();
            List<TransportEntity> transportEntityList = ReadExcel.readExcel(file);
            long end = System.currentTimeMillis();
            System.out.println(end-start+"毫秒");*/
            //替换poi
            long start = System.currentTimeMillis();
            List<TransportEntity> transportEntityList = ExcelImportUtil.importExcel(file.getInputStream(), TransportEntity.class, params);
            transportEntityList = transportEntityList.stream().filter(f -> f != null && f.getLicense_plate()!=null).collect(Collectors.toList());
            long end= System.currentTimeMillis();
            System.out.println(end-start+"毫秒");
            Map<Integer, List<TransportEntity>> resultMap = transpotrService.compareByExcel(transportEntityList);
            String strId = sequenceId.nextId();
            transpotrService.setJedisTransoportEntity(strId,resultMap);
            return Result.success(strId);
        } catch (Exception e) {
            return Result.failure(ResultCode.OPERATION_FAILURS);
        }
    }
}

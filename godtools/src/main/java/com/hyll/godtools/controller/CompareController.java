package com.hyll.godtools.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.hyll.godtools.pojo.TransportEntity;
import com.hyll.godtools.service.TranspotrService;
import com.hyll.godtools.util.ReadExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Api(tags = "compareApi")
@RequestMapping("/compare")
@Log4j2
@CrossOrigin
public class CompareController {

    @Autowired
    private TranspotrService transpotrService;

    /**
     * 上传需要比对的表进行比对，输出比对结果，若车牌号及装货时间存在相同则视为异常，同时输出源数据与导入的数据，若无则输出导入数据
     * @param file 需要进行对比的数据
     * @return 对比结果Json格式
     */
    @ResponseBody
    @ApiOperation(value="上传对比文件", notes="上传对比文件与数据库中数据进行比对", produces="application/json")
    @ApiImplicitParam(name = "file", value = "比对文件", paramType = "query", required = true, dataType = "file")
    @RequestMapping(value = "/upfile", method = RequestMethod.POST)
    public JSONObject compareData(@RequestParam(value = "file",required = false) MultipartFile file) {
        JSONObject jsonData = new JSONObject();
        if (!Objects.equals(file.getOriginalFilename(), "")) {
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            if (fileName.endsWith(".xlsx") | fileName.endsWith(".xls")) {
                List<TransportEntity> transportList;
                try {
                    transportList = ReadExcel.readExcel(file);
                    Map<Integer, List<TransportEntity>> resultMap = transpotrService.compareByExcel(transportList);
                    jsonData.putOpt("state",0);
                    jsonData.putOpt("data", resultMap);
                    return jsonData;
                }catch (Exception e){
                    e.printStackTrace();
                    jsonData.putOpt("state",-1);
                    return jsonData;
                }
            }else {
                jsonData.putOpt("state",-1);
                jsonData.putOpt("error","上传文件失败，文件格式错误");
                return jsonData;
            }
        }else {
            jsonData.putOpt("state",-1);
            jsonData.putOpt("error","上传文件失败，文件格式错误");
            return jsonData;
        }
    }
}
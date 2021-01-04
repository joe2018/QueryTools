package com.hyll.godtools.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Table(name = "tabletype")
@ApiModel(description = "表格导入状态")
public class TableType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 批次号
     *
     */
    @ApiModelProperty(value = "批次号",notes = "每次导入的一张Excel生成全局唯一的一个ID做为标记")
    @NotBlank(message = "批次号不能为空")
    private	String	batch_number;

    /**
     * 批次导入状态
     *
     */
    @ApiModelProperty(value = "导入状态",example = "1")
    @NotBlank(message = "状态不能为空")
    private	int	batch_type;

    /**
     * 批次导入时间
     *
     */
    @ApiModelProperty(value = "导入时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,  timezone="GMT+8")
    private Date batch_time;

    /**
     * 批次导入时间
     *
     */
    @ApiModelProperty(value = "文件md5")
    private String file_md5;


}

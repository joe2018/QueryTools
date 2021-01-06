package com.hyll.godtools.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("比对数据传递值")
public class Pager {
    @ApiModelProperty("操作比对后，返回的sequenceId")
    private String sequenceId;
    @ApiModelProperty("页码")
    private Integer page;
    @ApiModelProperty("页数")
    private Integer lim;
}

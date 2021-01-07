package com.hyll.godtools.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Table(name = "transport")
@ApiModel(description = "订运单信息")
public class TransportEntity implements Serializable {

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
     * 订单号
     *
     */
    @ApiModelProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    @Excel(name = "订单号")
    private	String	order_number;

    /**
     * 客户名称
     *
     */
    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private	String	customer_name;

    /**
     * 下单人
     *
     */
    @ApiModelProperty(value = "下单人")
    @Excel(name = "下单人")
    private	String	entrust_man_name;

    /**
     * 下单人联系电话
     *
     */
    @ApiModelProperty(value = "下单人联系电话")
    @Excel(name = "下单人联系电话")
    private	String	entrust_man_phone;

    /**
     * 发货人姓名
     *
     */
    @ApiModelProperty(value = "发货人姓名")
    @Excel(name = "发货人姓名")
    private	String	delivery_man_name;

    /**
     * 发货人联系电话
     *
     */
    @ApiModelProperty(value = "发货人联系电话")
    @Excel(name = "发货人联系电话")
    private	String	delivery_man_phone;

    /**
     * 收货人姓名
     *
     */
    @ApiModelProperty(value = "收货人姓名")
    @Excel(name = "收货人姓名")
    private	String	receive_man_name;

    /**
     * 收货人联系电话
     *
     */
    @ApiModelProperty(value = "收货人联系电话")
    @Excel(name = "收货人联系电话")
    private	String	receive_man_phone;

    /**
     * 装货地省
     *
     */
    @ApiModelProperty(value = "装货地省")
    @Excel(name = "装货地省")
    private	String	delivery_provinces;

    /**
     * 装货地市
     *
     */
    @ApiModelProperty(value = "装货地市")
    @Excel(name = "装货地市")
    private	String	delivery_city;

    /**
     * 装货地县/区
     *
     */
    @ApiModelProperty(value = "装货地县/区")
    @Excel(name = "装货地县/区")
    private	String	delivery_area;

    /**
     * 装货地详细地址
     *
     */
    @ApiModelProperty(value = "装货地详细地址")
    @Excel(name = "装货地详细地址")
    private	String	delivery_address;

    /**
     * 卸货地省
     *
     */
    @ApiModelProperty(value = "卸货地省")
    @Excel(name = "卸货地省")
    private	String	receive_provinces;

    /**
     * 卸货地市
     *
     */
    @ApiModelProperty(value = "卸货地市")
    @Excel(name = "卸货地市")
    private	String	receive_city;

    /**
     * 卸货地县/区
     *
     */
    @ApiModelProperty(value = "卸货地县/区")
    @Excel(name = "卸货地县/区")
    private	String	receive_area;

    /**
     * 卸货地详细地址
     *
     */
    @ApiModelProperty(value = "卸货地详细地址")
    @Excel(name = "卸货地详细地址")
    private	String	receive_address;

    /**
     * 货物类型
     *
     */
    @ApiModelProperty(value = "货物类型")
    @Excel(name = "货物类型")
    private	String	goods_type;

    /**
     * 货物数量
     *
     */
    @ApiModelProperty(value = "货物数量",example = "1")
    @Excel(name = "货物数量")
    private BigDecimal goods_quantity;

    /**
     * 货物单位
     *
     */
    @ApiModelProperty(value = "货物单位")
    @Excel(name = "货物单位")
    private	String	goods_units;

    /**
     * 应收运费
     *
     */
    @ApiModelProperty(value = "应收运费",example = "1")
    @Excel(name = "应收运费")
    private	BigDecimal	freight_receivable;

    /**
     * 发起收款金额
     *
     */
    @ApiModelProperty(value = "发起收款金额",example = "1")
    @Excel(name = "发起收款金额")
    private	BigDecimal	initiate_amount;

    /**
     * 运单号
     *
     */
    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private	String	tracking_number;

    /**
     * 承运商姓名
     *
     */
    @ApiModelProperty(value = "承运商姓名")
    @Excel(name = "承运商姓名")
    private	String	carrier_name;

    /**
     * 承运商电话
     *
     */
    @ApiModelProperty(value = "承运商电话")
    @Excel(name = "承运商电话")
    private	String	carrier_phone;

    /**
     * 下单时间
     *
     */
    @ApiModelProperty(value = "下单时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,  timezone="GMT+8")
    @Excel(name = "下单时间",format = "yyyy/MM/dd HH:mm:ss")
    private	Date	entrust_time;

    /**
     * 装货时间
     *
     */
    @ApiModelProperty(value = "装货时间")
    @NotBlank(message = "装货时间不能为空")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,  timezone="GMT+8")
    @Excel(name = "装货时间",format = "yyyy/MM/dd HH:mm:ss")
    private	Date	loading_time;

    /**
     * 装货日期
     *
     */

    @ApiModelProperty(value = "装货日期",notes = "通过装货时间获取")
    @JsonFormat(pattern="yyyy-MM-dd" ,  timezone="GMT+8")
    private	Date	loading_date;

    /**
     * 到达时间（小时）
     *
     */
    @ApiModelProperty(value = "到达时间（小时）",example = "1")
    @Excel(name = "到达时间（小时）")
    private	BigDecimal	transport_time;

    /**
     * 总装货量
     *
     */
    @ApiModelProperty(value = "总装货量",example = "1")
    @Excel(name = "总装货量")
    private	BigDecimal	loading_quantity;

    /**
     * 总卸货量
     *
     */
    @ApiModelProperty(value = "总卸货量",example = "1")
    @Excel(name = "总卸货量")
    private	BigDecimal	unloading_quantity;

    /**
     * 车牌号
     *
     */
    @ApiModelProperty(value = "车牌号")
    @NotBlank(message = "车牌号不能为空")
    @Excel(name = "车牌号")
    private	String	license_plate;

    /**
     * 司机姓名
     *
     */
    @ApiModelProperty(value = "司机姓名")
    @Excel(name = "司机姓名")
    private	String	driver_name;

    /**
     * 司机联系电话
     *
     */
    @ApiModelProperty(value = "司机联系电话")
    @Excel(name = "司机联系电话")
    private	String	driver_phone;

    /**
     * 现金支付金额
     *
     */
    @ApiModelProperty(value = "现金支付金额",example = "1")
    @Excel(name = "现金支付金额")
    private	BigDecimal	cash_payments;

    /**
     * 油卡支付金额
     *
     */
    @ApiModelProperty(value = "油卡支付金额",example = "1")
    @Excel(name = "油卡支付金额")
    private	BigDecimal	oil_payment;

    /**
     * 油卡卡号
     *
     */
    @ApiModelProperty(value = "油卡卡号")
    @Excel(name ="油卡卡号")
    private	String	oil_card;

    /**
     * 是否已付
     *
     */
    @ApiModelProperty(value = "是否已付")
    @Excel(name = "是否已付")
    private	String	payment_type;

    /**
     * 业务所属部门
     *
     */
    @ApiModelProperty(value = "业务所属部门")
    @Excel(name = "业务所属部门")
    private	String	business_department;

    /**
     * 是否开票
     *
     */
    @ApiModelProperty(value = "是否开票")
    @Excel(name = "是否开票")
    private	String	invoice_type;

    /**
     * 厂单号
     *
     */
    @ApiModelProperty(value = "厂单号")
    @Excel(name = "厂单号")
    private	String	merchant_number;

    /**
     * 货主
     *
     */
    @ApiModelProperty(value = "货主")
    @Excel(name = "货主")
    private	String	shipper;

    /**
     * 导入时间
     *
     */
    @ApiModelProperty(value = "导入时间",notes = "导入时自动生成")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss" ,  timezone="GMT+8")
    private	Date	occurrence_time;

}

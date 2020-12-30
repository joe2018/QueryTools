package com.hyll.godtools.util;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

import com.hyll.godtools.pojo.TransportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ReadExcel {

    public static List<TransportEntity> readExcel(MultipartFile file) throws IOException {
            try{
                    ExcelReader reader = ExcelUtil.getReader(file.getInputStream(), 0);
                    reader.addHeaderAlias("订单号","order_number");
                    reader.addHeaderAlias("客户名称","customer_name");
                    reader.addHeaderAlias("下单人","entrust_man_name");
                    reader.addHeaderAlias("下单人联系电话","entrust_man_phone");
                    reader.addHeaderAlias("发货人姓名","delivery_man_name");
                    reader.addHeaderAlias("发货人联系电话","delivery_man_phone");
                    reader.addHeaderAlias("收货人姓名","receive_man_name");
                    reader.addHeaderAlias("收货人联系电话","receive_man_phone");
                    reader.addHeaderAlias("装货地省","delivery_provinces");
                    reader.addHeaderAlias("装货地市","delivery_city");
                    reader.addHeaderAlias("装货地县/区","delivery_area");
                    reader.addHeaderAlias("装货地详细地址","delivery_address");
                    reader.addHeaderAlias("卸货地省","receive_provinces");
                    reader.addHeaderAlias("卸货地市","receive_city");
                    reader.addHeaderAlias("卸货地县/区","receive_area");
                    reader.addHeaderAlias("卸货地详细地址","receive_address");
                    reader.addHeaderAlias("货物类型","goods_type");
                    reader.addHeaderAlias("货物数量","goods_quantity");
                    reader.addHeaderAlias("货物单位","goods_units");
                    reader.addHeaderAlias("应收运费","freight_receivable");
                    reader.addHeaderAlias("发起收款金额","initiate_amount");
                    reader.addHeaderAlias("运单号","tracking_number");
                    reader.addHeaderAlias("承运商姓名","carrier_name");
                    reader.addHeaderAlias("承运商电话","carrier_phone");
                    reader.addHeaderAlias("下单时间","entrust_time");
                    reader.addHeaderAlias("装货时间","loading_time");
                    reader.addHeaderAlias("装货日期","loading_date");
                    reader.addHeaderAlias("到达时间（小时）","transport_time");
                    reader.addHeaderAlias("总装货量","loading_quantity");
                    reader.addHeaderAlias("总卸货量","unloading_quantity");
                    reader.addHeaderAlias("车牌号","license_plate");
                    reader.addHeaderAlias("司机姓名","driver_name");
                    reader.addHeaderAlias("司机联系电话","driver_phone");
                    reader.addHeaderAlias("现金支付金额","cash_payments");
                    reader.addHeaderAlias("油卡支付金额","oil_payment");
                    reader.addHeaderAlias("油卡卡号","oil_card");
                    reader.addHeaderAlias("是否已付","payment_type");
                    reader.addHeaderAlias("业务所属部门","business_department");
                    reader.addHeaderAlias("是否开票","invoice_type");
                    reader.addHeaderAlias("厂单号","merchant_number");
                    reader.addHeaderAlias("货主","shipper");
                    return reader.readAll(TransportEntity.class);
            }catch (Exception e){
                    return null;
            }
    }
}

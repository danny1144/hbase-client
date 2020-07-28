package com.siemens.hbase.hbaseclient.util;

/**
 * @description:
 * @create: 3/4/2020 11:10 AM
 **/
public class CompressorConstant {
    /**压气机折合转速*/
    public static final String  COMPRESSOR_CONVERT_RATE = "compressorConvertRate";
    /**压气机压比*/
    public static final String  COMPRESSOR_PRESS_RATE = "compressorPressRate";
    /**压气机折合流量*/
    public static final String  COMPRESSOR_CONVERT_FLOW = "compressorConvertFlow";
    /**压气机折合效率*/
    public static final String  COMPRESSOR_CONVERT_EFF = "compressorConvertEff";

    public static final String COMPRESSOR = "compressor";
    /**
     * 特性训练最大指标数
     * */
    public static final Long MAX_CHARACTER_NUMBER = 50000L;
    /**
     * 预测训练最大指标数
     * */
    public static final Long MAX_PREDICTION_NUMBER = 15000L;
}

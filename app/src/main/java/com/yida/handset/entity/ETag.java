package com.yida.handset.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gujiao on 2015/11/4.
 */
public class ETag {

    public static final String TYPE_JUMPER_WIRE = "跳纤";
    public static final String TYPE_SINGLE_JUMPER_WIRE = "单端标签跳纤";
    public static final String TYPE_JUMPER_CABLE = "跳缆";
    public static final String TYPE_LIGHT_ROUTER = "光分路由器";
    public static final String TYPE_OTHER = "其他";
    public static final String PORT_OTHER = "其他";
    public static final String PORT_ONE = "端口1";
    public static final String PORT_TWO = "端口2";
    public static final String PORT_IN = "输入端";
    public static final String PORT_OUT = "输出端";
    public static final Map<String, String> TYPE = new HashMap<>();
    static {
        TYPE.put("000001", TYPE_JUMPER_WIRE);
        TYPE.put("000010", TYPE_SINGLE_JUMPER_WIRE);
        TYPE.put("000011", TYPE_JUMPER_CABLE);
        TYPE.put("000100", TYPE_LIGHT_ROUTER);

        TYPE.put(TYPE_JUMPER_WIRE, "000001");
        TYPE.put(TYPE_SINGLE_JUMPER_WIRE, "000010");
        TYPE.put(TYPE_JUMPER_CABLE, "000011");
        TYPE.put(TYPE_LIGHT_ROUTER, "000100");

    }

    private int version = 0x0A; //电子标签格式版本，固定值
    private String productType; //光纤类型
                                //——fedcba=000001：跳纤；
                                //——fedcba=000010：单端标签跳纤；
                                //——fedcba=000011：跳缆；
                                //——fedcba=000100：光分路器；
                                //——fedcba=010000～011111：厂商 自定义；
                                // ——其他：预留
    /**
     *  如果产品类型不在标准范围内，而是自定义的，这个时候一定要把产品类型的二进
     *  制串放进这个属性里。产品类型是一个六位的二进制串
     * */
    private String productTypeOther; //如果光纤类型是其他，用来存储产品的二进制串
    private String port; //表示该端口是这条纤的哪个端口，01为端口1，10为端口2，单端跳纤只有01，光分路由器01为输入端，10为输出端。
    /**
     *  如果端口类型不在标准范围内，而是自定义的，这个时候一定要把端口类型的二进
     *  制串放进这个属性里。端口类型是一个两位的二进制串
     * */
    private String portOther; //如果端口类型是其他，用来存储端口的二进制串
    private String oid; //厂商标识，最大三个字节
    private String serialnumber; //序列号，最大十六个字节
    private int portNumber; //端口序号
    private int enterPortNumber; //入端口数.
                                 // ——跳纤： 1
                                 // ——单端标签跳纤： 1
                                 // ——跳缆：跳缆芯数
                                 // ——光分路器： 1或2
    private int outPortNumber; // 出端口数.
                            // ——跳纤： 1
                            // ——单端标签跳纤： 1
                            // ——跳缆：跳缆芯数
                            // ——光分路器：光分路器分支数

    private int operator; // 运营商
    private String operatorInfo; // 运营商扩展信息，最大四个字节
    private int CRC;
    private String extras; // 最大96个字节

    public String getProductTypeOther() {
        return productTypeOther;
    }

    public void setProductTypeOther(String productTypeOther) {
        this.productTypeOther = productTypeOther;
    }

    public String getPortOther() {
        return portOther;
    }

    public void setPortOther(String portOther) {
        this.portOther = portOther;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public int getCRC() {
        return CRC;
    }

    public void setCRC(int CRC) {
        this.CRC = CRC;
    }

    public String getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(String operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public int getOutPortNumber() {
        return outPortNumber;
    }

    public void setOutPortNumber(int outPortNumber) {
        this.outPortNumber = outPortNumber;
    }

    public int getEnterPortNumber() {
        return enterPortNumber;
    }

    public void setEnterPortNumber(int enterPortNumber) {
        this.enterPortNumber = enterPortNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

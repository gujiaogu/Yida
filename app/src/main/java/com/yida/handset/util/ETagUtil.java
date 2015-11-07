package com.yida.handset.util;

import com.yida.handset.entity.ETag;

import java.util.Arrays;

/**
 * Created by gujiao on 2015/11/4.
 */
public class ETagUtil {

    public static ETag paseETag(byte[] eTagByte) {
        ETag eTag = new ETag();
        //版本
        eTag.setVersion(eTagByte[0]);
        String product = Integer.toBinaryString(eTagByte[1]).substring(24);
        String productType = ETag.TYPE.get(product.substring(2));
        if (productType == null) {
            eTag.setProductType(ETag.TYPE_OTHER);
            eTag.setProductTypeOther(product.substring(2));
        } else {
            eTag.setProductType(productType);
        }
        String port = product.substring(0, 2);
        if (productType == null) {
            eTag.setPort(ETag.PORT_OTHER);
            eTag.setPortOther(port);
        } else {
            switch (productType) {
                case ETag.TYPE_JUMPER_WIRE:
                    if ("01".equals(port)) {
                        eTag.setPort(ETag.PORT_ONE);
                    } else if ("10".equals(port)) {
                        eTag.setPort(ETag.PORT_TWO);
                    } else {
                        eTag.setPort(ETag.PORT_OTHER);
                        eTag.setPortOther(port);
                    }
                    break;
                case ETag.TYPE_SINGLE_JUMPER_WIRE:
                    eTag.setPort(ETag.PORT_ONE);
                    break;
                case ETag.TYPE_JUMPER_CABLE:
                    if ("01".equals(port)) {
                        eTag.setPort(ETag.PORT_ONE);
                    } else if ("10".equals(port)) {
                        eTag.setPort(ETag.PORT_TWO);
                    } else {
                        eTag.setPort(ETag.PORT_OTHER);
                        eTag.setPortOther(port);
                    }
                    break;
                case ETag.TYPE_LIGHT_ROUTER:
                    if ("01".equals(port)) {
                        eTag.setPort(ETag.PORT_IN);
                    } else if ("10".equals(port)) {
                        eTag.setPort(ETag.PORT_OUT);
                    } else {
                        eTag.setPort(ETag.PORT_OTHER);
                        eTag.setPortOther(port);
                    }
                    break;
                default:
                    eTag.setPort(ETag.PORT_OTHER);
                    break;
            }
        }


        eTag.setOid(new String(Arrays.copyOfRange(eTagByte, 2, 5)).trim());
        eTag.setSerialnumber(new String(Arrays.copyOfRange(eTagByte, 5, 21)).trim());
        eTag.setPortNumber(Integer.parseInt(String.valueOf(eTagByte[21]), 16));
        eTag.setEnterPortNumber(Integer.parseInt(String.valueOf(eTagByte[22]), 16));
        eTag.setOutPortNumber(Integer.parseInt(String.valueOf(eTagByte[23]), 16));
        eTag.setOperator(eTagByte[24]);
        eTag.setOperatorInfo(new String(Arrays.copyOfRange(eTagByte, 25, 29)).trim());
        eTag.setCRC(eTagByte[31]);
        eTag.setExtras(new String(Arrays.copyOfRange(eTagByte, 32, eTagByte.length)).trim());
        return eTag;
    }

    public static byte[] toBytes(ETag eTag) {
        byte[] eTagByte = new byte[128];
        eTagByte[0] = 0x0A;
        String productType = eTag.getProductType();
        StringBuilder builder = new StringBuilder();
        String port = eTag.getPort();
        switch (productType) {
            case ETag.TYPE_JUMPER_WIRE:
                switch (port) {
                    case ETag.PORT_ONE:
                        builder.append("01").append(ETag.TYPE.get(ETag.TYPE_JUMPER_WIRE));
                        break;
                    case ETag.PORT_TWO:
                        builder.append("10").append(ETag.TYPE.get(ETag.TYPE_JUMPER_WIRE));
                        break;
                    case ETag.PORT_OTHER:
                        builder.append(eTag.getPortOther()).append(ETag.TYPE.get(ETag.TYPE_JUMPER_WIRE));
                        break;
                    default:
                        break;
                }
                break;
            case ETag.TYPE_SINGLE_JUMPER_WIRE:
                builder.append("01").append(ETag.TYPE.get(ETag.TYPE_SINGLE_JUMPER_WIRE));
                break;
            case ETag.TYPE_JUMPER_CABLE:
                switch (port) {
                    case ETag.PORT_ONE:
                        builder.append("01").append(ETag.TYPE.get(ETag.TYPE_JUMPER_CABLE));
                        break;
                    case ETag.PORT_TWO:
                        builder.append("10").append(ETag.TYPE.get(ETag.TYPE_JUMPER_CABLE));
                        break;
                    case ETag.PORT_OTHER:
                        builder.append(eTag.getPortOther()).append(ETag.TYPE.get(ETag.TYPE_JUMPER_CABLE));
                        break;
                    default:
                        break;
                }
                break;
            case ETag.TYPE_LIGHT_ROUTER:
                switch (port) {
                    case ETag.PORT_IN:
                        builder.append("01").append(ETag.TYPE.get(ETag.TYPE_LIGHT_ROUTER));
                        break;
                    case ETag.PORT_OUT:
                        builder.append("10").append(ETag.TYPE.get(ETag.TYPE_LIGHT_ROUTER));
                        break;
                    case ETag.PORT_OTHER:
                        builder.append(eTag.getPortOther()).append(ETag.TYPE.get(ETag.TYPE_LIGHT_ROUTER));
                        break;
                    default:
                        break;
                }
                break;
            case ETag.TYPE_OTHER:
                builder.append(eTag.getPortOther()).append(eTag.getProductTypeOther());
                break;
            default:
                break;
        }
        eTagByte[1] = (byte) Integer.parseInt(builder.toString(), 2);
        byte[] oid = eTag.getOid().getBytes();
        if (oid.length > 3) {
            System.out.println("=====请检查数据=====oid");
            return new byte[0];
        } else {
            System.arraycopy(oid, 0, eTagByte, 2, oid.length);
        }
        byte[] serialNumber = eTag.getSerialnumber().getBytes();
        if (serialNumber.length > 16) {
            System.out.println("=====请检查数据=====serialNumber");
            return new byte[0];
        } else {
            System.arraycopy(serialNumber, 0, eTagByte, 5, serialNumber.length);
        }
        eTagByte[21] = (byte) eTag.getPortNumber();
        eTagByte[22] = (byte) eTag.getEnterPortNumber();
        eTagByte[23] = (byte) eTag.getOutPortNumber();
        eTagByte[24] = (byte) eTag.getOperator();
        byte[] operatorInfo = eTag.getOperatorInfo().getBytes();
        if (operatorInfo.length > 4) {
            System.out.println("=====请检查数据=====operatorInfo");
            return new byte[0];
        } else {
            System.arraycopy(operatorInfo, 0, eTagByte, 25, operatorInfo.length);
        }
        eTagByte[31] = (byte) eTag.getCRC();
        byte[] extras = eTag.getExtras().getBytes();
        if (extras.length > 96) {
            System.out.println("=====请检查数据=====extras");
            return new byte[0];
        } else {
            System.arraycopy(extras, 0, eTagByte, 32, extras.length);
        }
        return eTagByte;
    }
}

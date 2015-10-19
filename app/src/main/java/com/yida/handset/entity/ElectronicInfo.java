package com.yida.handset.entity;

/**
 * Created by gujiao on 2015/10/17.
 */
public class ElectronicInfo {
    private int frameNo;
    private int boardNo;
    private int portNo;
    private Electronic electronicIdInfo;

    public int getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public int getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(int boardNo) {
        this.boardNo = boardNo;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public Electronic getElectronicIdInfo() {
        return electronicIdInfo;
    }

    public void setElectronicIdInfo(Electronic electronicIdInfo) {
        this.electronicIdInfo = electronicIdInfo;
    }
}

package com.tencent.qqnt.kernel.nativeinterface;

public final class StFeed {
    public StFeedCellCommon cellCommon = new StFeedCellCommon();
    public StFeedCellUserInfo cellUserInfo = new StFeedCellUserInfo();
    public StFeedCellTitle cellTitle = new StFeedCellTitle();
    public StFeedCellSummary cellSummary = new StFeedCellSummary();
    public StFeedCellMedia cellMedia = new StFeedCellMedia();
    public StFeedCellLBS cellLbs = new StFeedCellLBS();
    public StFeedCellComment cellComment = new StFeedCellComment();
    public StFeedCellLike cellLike = new StFeedCellLike();
    public StCellBottomRecomm cellBottomRecomm = new StCellBottomRecomm();
    public StFeedCellSpaceInfo cellSpaceInfo = new StFeedCellSpaceInfo();
    public StFeedCellVisitor cellVisitor = new StFeedCellVisitor();
    public StFeedCellQunInfo cellQunInfo = new StFeedCellQunInfo();

    public StCellBottomRecomm getCellBottomRecomm() {
        return this.cellBottomRecomm;
    }

    public void setCellBottomRecomm(StCellBottomRecomm stCellBottomRecomm) {
        this.cellBottomRecomm = stCellBottomRecomm;
    }

    public StFeedCellComment getCellComment() {
        return this.cellComment;
    }

    public void setCellComment(StFeedCellComment stFeedCellComment) {
        this.cellComment = stFeedCellComment;
    }

    public StFeedCellCommon getCellCommon() {
        return this.cellCommon;
    }

    public void setCellCommon(StFeedCellCommon stFeedCellCommon) {
        this.cellCommon = stFeedCellCommon;
    }

    public StFeedCellLBS getCellLbs() {
        return this.cellLbs;
    }

    public void setCellLbs(StFeedCellLBS stFeedCellLBS) {
        this.cellLbs = stFeedCellLBS;
    }

    public StFeedCellLike getCellLike() {
        return this.cellLike;
    }

    public void setCellLike(StFeedCellLike stFeedCellLike) {
        this.cellLike = stFeedCellLike;
    }

    public StFeedCellMedia getCellMedia() {
        return this.cellMedia;
    }

    public void setCellMedia(StFeedCellMedia stFeedCellMedia) {
        this.cellMedia = stFeedCellMedia;
    }

    public StFeedCellQunInfo getCellQunInfo() {
        return this.cellQunInfo;
    }

    public void setCellQunInfo(StFeedCellQunInfo stFeedCellQunInfo) {
        this.cellQunInfo = stFeedCellQunInfo;
    }

    public StFeedCellSpaceInfo getCellSpaceInfo() {
        return this.cellSpaceInfo;
    }

    public void setCellSpaceInfo(StFeedCellSpaceInfo stFeedCellSpaceInfo) {
        this.cellSpaceInfo = stFeedCellSpaceInfo;
    }

    public StFeedCellSummary getCellSummary() {
        return this.cellSummary;
    }

    public void setCellSummary(StFeedCellSummary stFeedCellSummary) {
        this.cellSummary = stFeedCellSummary;
    }

    public StFeedCellTitle getCellTitle() {
        return this.cellTitle;
    }

    public void setCellTitle(StFeedCellTitle stFeedCellTitle) {
        this.cellTitle = stFeedCellTitle;
    }

    public StFeedCellUserInfo getCellUserInfo() {
        return this.cellUserInfo;
    }

    public void setCellUserInfo(StFeedCellUserInfo stFeedCellUserInfo) {
        this.cellUserInfo = stFeedCellUserInfo;
    }

    public StFeedCellVisitor getCellVisitor() {
        return this.cellVisitor;
    }

    public void setCellVisitor(StFeedCellVisitor stFeedCellVisitor) {
        this.cellVisitor = stFeedCellVisitor;
    }
}

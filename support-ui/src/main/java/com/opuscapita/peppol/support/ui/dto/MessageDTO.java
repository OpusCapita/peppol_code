package com.opuscapita.peppol.support.ui.dto;


import com.opuscapita.peppol.support.ui.domain.Direction;
import com.opuscapita.peppol.support.ui.domain.MessageStatus;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by KACENAR1 on 2014.04.29..
 */
public class MessageDTO {

    private Integer id;

    private String senderIdentifier;

    private String senderName;

    private String recipientId;

    private String invoiceNumber;

    private Date invoiceDate;

    private Date dueDate;

    private boolean resolvedManually;

    private String resolvedComment;

    private MessageStatus status;

    private Timestamp arrivedTimeStamp;

    private Timestamp deliveredTimeStamp;

    private String errorMessage;

    private Integer fileId;

    private Long fileSize;

    private String documentType;

    private Direction direction;

    private String fileName;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    public void setSenderIdentifier(String senderIdentifier) {
        this.senderIdentifier = senderIdentifier;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isResolvedManually() {
        return resolvedManually;
    }

    public void setResolvedManually(boolean resolvedManually) {
        this.resolvedManually = resolvedManually;
    }

    public String getResolvedComment() {
        return resolvedComment;
    }

    public void setResolvedComment(String resolvedComment) {
        this.resolvedComment = resolvedComment;
    }

    public Timestamp getArrivedTimeStamp() {
        return arrivedTimeStamp;
    }

    public void setArrivedTimeStamp(Timestamp arrivedTimeStamp) {
        this.arrivedTimeStamp = arrivedTimeStamp;
    }

    public Timestamp getDeliveredTimeStamp() {
        return deliveredTimeStamp;
    }

    public void setDeliveredTimeStamp(Timestamp deliveredTimeStamp) {
        this.deliveredTimeStamp = deliveredTimeStamp;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

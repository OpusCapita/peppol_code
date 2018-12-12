package com.opuscapita.peppol.commons.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
    @JoinColumn(name = "sender_id")
    private Customer sender;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "resolved_manually")
    private boolean resolvedManually;

    @Column(name = "resolved_comment")
    private String resolvedComment;

    @Column(name = "origin")
    private String originalSource;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private Direction direction;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Sort(type = SortType.NATURAL)
    private SortedSet<FileInfo> files;

    public String getOriginalSource() {
        return originalSource;
    }

    public void setOriginalSource(String originalSource) {
        if (StringUtils.isNotBlank(originalSource)) {
            this.originalSource = originalSource;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
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

    public Set<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(SortedSet<FileInfo> files) {
        this.files = files;
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

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}

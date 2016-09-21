package com.opuscapita.peppol.support.ui.service;

import com.opuscapita.peppol.support.ui.domain.AmqpStatus;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.20.11
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public interface StatusService {

    public List<AmqpStatus> getAmqpStatuses();

    public String getOutboundProcessId();

    public String getQuartzStatus();

}

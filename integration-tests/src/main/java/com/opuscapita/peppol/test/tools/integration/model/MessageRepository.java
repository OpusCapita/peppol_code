package com.opuscapita.peppol.test.tools.integration.model;

import com.opuscapita.peppol.commons.model.Message;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by gamanse1 on 2016.12.12..
 */
public interface MessageRepository extends CrudRepository<Message, Integer> {
}

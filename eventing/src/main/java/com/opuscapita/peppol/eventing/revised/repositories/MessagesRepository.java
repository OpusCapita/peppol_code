package com.opuscapita.peppol.eventing.revised.repositories;

import com.opuscapita.peppol.commons.revised_model.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessagesRepository extends CrudRepository<Message, String> {
}

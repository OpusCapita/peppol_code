package com.opuscapita.peppol.email.db;

import com.opuscapita.peppol.commons.model.EmailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Sergejs.Roze
 */
public interface EmailInfoRepository extends JpaRepository<EmailInfo, Integer> {}

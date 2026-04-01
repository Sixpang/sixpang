package org.sixpang.hubservice.domain.repository;

import org.sixpang.hubservice.domain.model.entity.Hub;

public interface HubRepository {
    Hub save(Hub hub);
}

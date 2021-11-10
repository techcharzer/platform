package com.cz.platform.unique;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UniqueUtilityRepository extends MongoRepository<UniqueUtilityEntity, String> {

}

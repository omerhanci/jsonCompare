package com.waes.jsonCompare.repository;

import com.waes.jsonCompare.entity.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiffRepository extends JpaRepository<Data, Long> {

}

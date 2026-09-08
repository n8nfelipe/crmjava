package com.acme.crm.domain.repository;

import com.acme.crm.domain.model.PipelineStage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PipelineStageRepository extends JpaRepository<PipelineStage, UUID> {}

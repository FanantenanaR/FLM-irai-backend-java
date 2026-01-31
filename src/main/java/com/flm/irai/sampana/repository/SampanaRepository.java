package com.flm.irai.sampana.repository;

import com.flm.irai.sampana.model.Sampana;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SampanaRepository extends JpaRepository<Sampana, UUID> {

    // Recherche par Fiangonana
    List<Sampana> findByFiangonanaId(UUID fiangonanaId);

    Page<Sampana> findByFiangonanaId(UUID fiangonanaId, Pageable pageable);

    // Recherche par type
    List<Sampana> findByTypeId(UUID typeId);

    Page<Sampana> findByTypeId(UUID typeId, Pageable pageable);

    // Recherche par Fiangonana et type
    List<Sampana> findByFiangonanaIdAndTypeId(UUID fiangonanaId, UUID typeId);

    // Compte le nombre de Sampana par Fiangonana
    long countByFiangonanaId(UUID fiangonanaId);

    // Recherche avec filtres optionnels
    @Query("SELECT s FROM Sampana s WHERE " +
           "(:fiangonanaId IS NULL OR s.fiangonana.id = :fiangonanaId) AND " +
           "(:typeId IS NULL OR s.type.id = :typeId)")
    Page<Sampana> findWithFilters(
            @Param("fiangonanaId") UUID fiangonanaId,
            @Param("typeId") UUID typeId,
            Pageable pageable);
}

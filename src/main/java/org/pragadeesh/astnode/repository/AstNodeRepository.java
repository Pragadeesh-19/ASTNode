package org.pragadeesh.astnode.repository;

import org.pragadeesh.astnode.entity.AstNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AstNodeRepository extends JpaRepository<AstNode, UUID> {

}

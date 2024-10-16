package org.pragadeesh.astnode.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "rules")
@Data
public class Rule {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "root_node_id", referencedColumnName = "id")
    private AstNode rootNode;

}

package org.pragadeesh.astnode.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "ast_nodes")
@Data
public class AstNode {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String type;

    private String values;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "left_node_id")
    private AstNode left;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "right_node_id")
    private AstNode right;

    public AstNode(String type, String values, AstNode left, AstNode right) {
        this.type = type;
        this.values = values;
        this.left = left;
        this.right = right;
    }

    public AstNode(String type, String values) {
        this.type = type;
        this.values = values;
    }

    public AstNode() {

    }
}

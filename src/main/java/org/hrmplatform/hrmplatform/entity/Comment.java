package org.hrmplatform.hrmplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private String author;
    private String authorImage;
    private String position;
    
    @ManyToOne
    @JoinColumn(name = "company_id") // Yorumun hangi şirkete ait olduğunu gösterir
    private Company company;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
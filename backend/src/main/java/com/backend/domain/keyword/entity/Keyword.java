package com.backend.domain.keyword.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keyword")
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private KeywordCategory category;

    @Column(name = "name")
    private String name;


    public static Keyword ofKeyword(Long id, String name) {
        Keyword keyword = new Keyword();
        keyword.id = id;
        keyword.name = name;
        keyword.category = null;
        return keyword;
    }

}

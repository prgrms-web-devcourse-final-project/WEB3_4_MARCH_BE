package com.backend.domain.keyword.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // JSON 역직렬화 지원 생성자
    @JsonCreator
    public Keyword(@JsonProperty("id") Long id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
        this.category = null; // 프론트에서 category는 null로 보낼 수 있음
    }

    public static Keyword ofKeyword(Long id, String name) {
        Keyword keyword = new Keyword();
        keyword.id = id;
        keyword.name = name;
        keyword.category = null;
        return keyword;
    }

}

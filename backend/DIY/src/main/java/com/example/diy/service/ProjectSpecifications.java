package com.example.diy.service;

import com.example.diy.model.Project;
import com.example.diy.model.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProjectSpecifications {
    public static Specification<Project> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return null;
            }

            query.distinct(true);
            Join<Project, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(tagsJoin.get("name")), pattern)
            );
        };
    }

    // 2. פילטר קטגוריות
    public static Specification<Project> categoryIn(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null; // אין סינון אם הרשימה ריקה
            }
            // התנאי: ה-ID של הקטגוריה נמצא בתוך הרשימה שקיבלנו
            return root.get("category").get("id").in(categoryIds);
        };
    }

}

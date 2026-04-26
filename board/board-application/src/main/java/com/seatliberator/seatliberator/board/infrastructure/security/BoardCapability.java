package com.seatliberator.seatliberator.board.infrastructure.security;

import com.seatliberator.seatliberator.identity.core.role.Capability;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardCapability implements Capability {
    POST_LIST("post.list", "게시글 목록 조회"),
    POST_READ("post.read", "게시글 조회"),
    POST_CREATE("post.create", "게시글 작성"),
    OWNED_POST_UPDATE("owned.post.update", "본인 작성 글 수정"),
    OWNED_POST_DELETE("owned.post.delete", "본인 작성 글 삭제"),
    POST_MANAGE("post.manage", "게시글 관리"),

    COMMENT_CREATE("comment.create", "댓글 작성"),
    OWNED_COMMENT_UPDATE("owned.comment.update", "본인 작성 댓글 수정"),
    OWNED_COMMENT_DELETE("owned.comment.delete", "본인 작성 댓글 삭제"),
    COMMENT_MANAGE("comment.manage", "댓글 관리"),

    CATEGORY_LIST("category.list", "카테고리 목록 조회"),
    CATEGORY_CREATE("category.create", "카테고리 생성"),
    CATEGORY_MANAGE("category.manage", "카테고리 관리");

    private final String scope;
    private final String description;

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public String description() {
        return description;
    }
}

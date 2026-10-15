package com.an.campustaskapi.task;

/**
 * 经过 JWT 验证的当前用户身份，只保存业务需要的信息。
 * 不存放密码或密码哈希。
 */
public record AuthenticatedUser(Long id, String username) {
}

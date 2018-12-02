package com.lingc.util;

import org.neo4j.graphdb.RelationshipType;

/**
 * 关系 枚举
 * @author wzp
 * @description 歌曲信息管理中的关系
 * @time Create at 2018/12/2 22:24
 * @modified
 */
public enum RelationshipEnum implements RelationshipType {
    /**
     * 发布
     */
    PUBLISH,
    /**
     * 包含
     */
    CONTAIN
}

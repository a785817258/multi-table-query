package com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent;

import com.lzb.mpmt.service.multiwrapper.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.MultiException;
import com.lzb.mpmt.service.multiwrapper.util.MultiTableRelationFactory;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 副表,和主副表对应关系信息
 *
 * @param <SUB> 副表泛型
 * @author Administrator
 */
@Data
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class MultiWrapperSubAndRelation<SUB> implements IMultiWrapperSubAndRelationTreeNode {

    public static MultiTableRelationFactory MULTI_TABLE_RELATION_FACTORY;

    /**
     * wrapperSub 为空表示主表临时对象
     */
    public MultiWrapperSubAndRelation(JoinTypeEnum joinType, String relationCode, MultiWrapperSub<SUB> wrapperSub) {
        this.joinType = joinType;
        this.relationCode = relationCode;
        this.wrapperSub = wrapperSub;
    }

    /**
     * 聚合方式
     */
    private JoinTypeEnum joinType;

    /**
     * 关系 ClassRelation.code {@link MultiTableRelation#getCode()}
     */
    private String relationCode;

    /**
     * 副表信息
     */
    private MultiWrapperSub<SUB> wrapperSub;

    /**
     * relationCode对应的关系表1
     */
    private String tableNameThis;
    /**
     * relationCode对应的关系表2
     */
    private String tableNameOther;
    /**
     * This -> Other 的关系是one还是many
     */
    private ClassRelationOneOrManyEnum subTableOneOrMany;

    public String getSqlJoin(String mainTableName) {
        if (MULTI_TABLE_RELATION_FACTORY == null) {
            throw new MultiException("请先初始化表关系 MultiWrapperSubAndRelation.MultiTableRelationFactory");
        }
        MultiTableRelation relation = MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(relationCode);
        String subTableName = wrapperSub.getTableName();

        String sqlWhereProps = getWrapperSub().getSqlWhereProps();
        sqlWhereProps = MultiUtil.isEmpty(sqlWhereProps) ? MultiConstant.Strings.EMPTY : " and " + sqlWhereProps;
        return joinType.getSql() + subTableName + " on " + relation.getTableName1() + "." + relation.getClass1KeyProp() + " = " + relation.getTableName2() + "." + relation.getClass2KeyProp() + sqlWhereProps;
    }

    @Override
    public Class<?> getTableClassThis() {
        return this.wrapperSub.getClazz();
    }

    @Override
    public MultiWrapperSelect<?, ?> getMultiWrapperSelectInfo() {
        return getWrapperSub();
    }
}
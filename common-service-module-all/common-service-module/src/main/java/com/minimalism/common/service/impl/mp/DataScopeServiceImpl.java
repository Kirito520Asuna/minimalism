package com.minimalism.common.service.impl.mp;

import com.minimalism.mp.abs.service.impl.DataScopeDefaultServiceImpl;
import com.minimalism.base.constant.table.TableConstants;
import com.minimalism.mp.aop.domain.DataScopeAboutTable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service @Primary
public class DataScopeServiceImpl extends DataScopeDefaultServiceImpl {
    @Override
    public DataScopeAboutTable fetchDataScopeAboutTable() {
        DataScopeAboutTable build = DataScopeAboutTable.builder()
                .userIdName(TableConstants.USER_COL_USER_ID)
                .deptIdName(TableConstants.DEPT_COL_DEPT_ID)

                .roleDeptTableName(TableConstants.role_dept)
                .roleDeptTableRoleIdName(TableConstants.ROLE_DEPT_COL_ROLE_ID)
                .roleDeptTableDeptIdName(TableConstants.ROLE_DEPT_COL_DEPT_ID)

                .deptAncestorsName(TableConstants.dept_ancestor)
                .deptAncestorsIdName(TableConstants.DEPT_ANCESTOR_COL_DEPT_ID)
                .deptAncestorsParentIdName(TableConstants.DEPT_ANCESTOR_COL_DEPT_PARENT_ID)
                .build();
        return build;
    }
}

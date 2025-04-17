package com.minimalism.abstractinterface.service.impl;

import com.minimalism.abstractinterface.service.DataScopeService;
import com.minimalism.mp.aop.domain.DataScopeAboutTable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public abstract class DataScopeDefaultServiceImpl implements DataScopeService {

    @Override
    public Map<String, String> fetchDataScopeSqlBuildFormatMap() {
        DataScopeAboutTable dataScopeAboutTable = fetchDataScopeAboutTable();
        if (dataScopeAboutTable == null) {
            dataScopeAboutTable = new DataScopeAboutTable();
        }

        String userIdName = dataScopeAboutTable.getUserIdName();
        String deptIdName = dataScopeAboutTable.getDeptIdName();

        String roleDeptTableName = dataScopeAboutTable.getRoleDeptTableName();
        String roleDeptTableRoleIdName = dataScopeAboutTable.getRoleDeptTableRoleIdName();
        String roleDeptTableDeptIdName = dataScopeAboutTable.getRoleDeptTableDeptIdName();

        String deptAncestorsName = dataScopeAboutTable.getDeptAncestorsName();
        String deptAncestorsIdName = dataScopeAboutTable.getDeptAncestorsIdName();
        String deptAncestorsParentIdName = dataScopeAboutTable.getDeptAncestorsParentIdName();

        String isConditionsValue = " OR %s.`" + deptIdName + "` = 0 ";
        String dataScopeCustomScopeCustomIdsTrueValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
                roleDeptTableName + "` WHERE `" + roleDeptTableRoleIdName + "` in (%s) ) ";
        String dataScopeCustomScopeCustomIdsFalseValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + roleDeptTableDeptIdName + "` FROM `" +
                roleDeptTableDeptIdName + "` WHERE `" + roleDeptTableRoleIdName + "`  = %s ) ";
        String dataScopeDeptDeptAliasTrueValue = " OR %s.`" + deptIdName + "` = %s ";
        String dataScopeDeptAndChildDeptAliasTrueValue = " OR %s.`" + deptIdName + "` IN ( SELECT `" + deptAncestorsParentIdName + "` FROM `" +
                deptAncestorsName + "` WHERE `" + deptAncestorsIdName + "` = %s ) ";
        String dataScopeSelfUserAliasTrueValue = " OR %s.`" + userIdName + "` = %s ";

        Map<String, String> dataScopeSqlBuildFormatMap = new LinkedHashMap<>();
        dataScopeSqlBuildFormatMap.put(IS_CONDITIONS, isConditionsValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_TRUE, dataScopeCustomScopeCustomIdsTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_CUSTOM_SCOPE_CUSTOM_IDS_FALSE, dataScopeCustomScopeCustomIdsFalseValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_DEPT_DEPT_ALIAS_TRUE, dataScopeDeptDeptAliasTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_DEPT_AND_CHILD_DEPT_ALIAS_TRUE, dataScopeDeptAndChildDeptAliasTrueValue);
        dataScopeSqlBuildFormatMap.put(DATA_SCOPE_SELF_USER_ALIAS_TRUE, dataScopeSelfUserAliasTrueValue);

        return dataScopeSqlBuildFormatMap;
    }
}

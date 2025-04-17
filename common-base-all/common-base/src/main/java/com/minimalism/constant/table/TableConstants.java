package com.minimalism.constant.table;

public interface TableConstants {
    /*==============================table-start==============================*/
    //命名 规范：简化表名小写
    String user = "sys_user";
    String user_role = "sys_user_role";
    String role = "sys_role";
    String role_menu = "sys_role_menu";
    String menu = "sys_menu";
    String menu_ancestor = "sys_menu_ancestor";
    /*=======================================================================*/
    String dept = "sys_dept";
    String user_dept = "sys_user_dept";
    String role_dept = "sys_role_dept";
    String dept_ancestor = "sys_dept_ancestor";
    /*=======================================================================*/
    String dict_data = "sys_dict_data";
    String dict_type = "sys_dict_type";
    /*=======================================================================*/
    String gen_table = "gen_table";
    String gen_table_column = "gen_table_column";
    /*=======================================================================*/
    String file_info = "file_info";
    String file_part = "file_part";
    /*=======================================================================*/
    String job = "sys_job";
    String job_log = "sys_job_log";
    /*=======================================================================*/
    String apply = "apply";
    String friend = "friend";
    String message = "message";

    String chat_message = "chat_message";
    String chat_user = "chat_user";
    String chat_window = "chat_window";
    /*===============================table-end===============================*/
    /*=======================================================================*/
    /*=======================================================================*/
    /*===========================table-column-start==========================*/
    //命名 规范：简化表名大写_COL_列名大写
    String USER_COL_USER_ID = "user_id";
    /*=======================================================================*/
    String DEPT_COL_DEPT_ID = "dept_id";
    /*=======================================================================*/
    String ROLE_DEPT_COL_ROLE_ID = "role_id";
    String ROLE_DEPT_COL_DEPT_ID = "dept_id";
    /*=======================================================================*/
    String DEPT_ANCESTOR_COL_DEPT_ID = "dept_id";
    String DEPT_ANCESTOR_COL_DEPT_PARENT_ID = "dept_parent_id";
    /*=======================================================================*/
    /*============================table-column-end===========================*/
}

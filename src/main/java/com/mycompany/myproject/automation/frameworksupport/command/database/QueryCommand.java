package com.mycompany.myproject.automation.frameworksupport.command.database;

import com.pwc.core.framework.command.DatabaseCommand;

public enum QueryCommand implements DatabaseCommand {

    ALL_COLUMN_NAMES_BY_TABLE_NAME("select unique(COLUMN_NAME) from ALL_TAB_COLUMNS where TABLE_NAME=?");

    private String sqlStatement;

    QueryCommand(String sql) {
        sqlStatement = sql;
    }

    @Override
    public String sql() {
        return this.sqlStatement;
    }

}

package com.mycompany.myproject.automation.frameworksupport.command.database;

import com.pwc.core.framework.command.DatabaseCommand;

public enum DeleteCommand implements DatabaseCommand {

    FUTURE("delete from ");

    private String sqlStatement;

    DeleteCommand(String sql) {
        sqlStatement = sql;
    }

    @Override
    public String sql() {
        return this.sqlStatement;
    }

}

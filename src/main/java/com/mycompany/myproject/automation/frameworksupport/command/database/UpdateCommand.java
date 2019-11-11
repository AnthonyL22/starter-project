package com.mycompany.myproject.automation.frameworksupport.command.database;

import com.pwc.core.framework.command.DatabaseCommand;

public enum UpdateCommand implements DatabaseCommand {

    FUTURE("update ");

    private String sqlStatement;

    UpdateCommand(String sql) {
        sqlStatement = sql;
    }

    @Override
    public String sql() {
        return this.sqlStatement;
    }

}

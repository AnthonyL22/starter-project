package com.mycompany.myproject.automation.data.provider;

import org.testng.annotations.DataProvider;

public class ExcelDataProvider {

    @DataProvider(name = "exampleExcelData")
    public static Object[][] exampleExcelData() {
        return ExcelUtils.getTableArray("developer_info.xlsx", "info", 3);
    }

}

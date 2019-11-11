package com.mycompany.myproject.automation.data.provider;

import com.pwc.core.framework.util.dataprovider.ReadExcelUtils;
import org.testng.annotations.DataProvider;

public class ExcelDataProvider {

    @DataProvider(name = "exampleExcelData")
    public static Object[][] exampleExcelData() {
        return ReadExcelUtils.getTableArray("/data", "developer_info.xlsx", "info", 3);
    }

}

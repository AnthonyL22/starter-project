try {

    /**
     * Special Utility to pass in and use any Selenium GRID URL defined in a Jenkins job.
     * To Use: add pre-step to Jenkins job that runs a Groovy file
     *
     * Custom Jenkins Variables needed in Jenkins job:
     *      TEST_ENVIRONMENT
     *      GRID_URL
     */
    def jenkinsBase = System.getenv('HUDSON_HOME')
    def jobName = System.getenv('JOB_NAME')
    def environment = System.getenv('TEST_ENVIRONMENT')
    def gridUrl = System.getenv('GRID_URL')

    Properties props = new Properties()
    File gridPropertiesFile = new File(jenkinsBase + "/workspace/" + jobName + "/src/test/resources/config/" + environment + "/grid.properties")
    if (gridPropertiesFile.exists()) {

        println("Updating grid.properties with new GRID URL=" + gridUrl)

        props.load(gridPropertiesFile.newDataInputStream())
        props.setProperty('grid.enabled', 'true')
        props.setProperty('grid.hub.url', gridUrl)
        gridPropertiesFile.delete()
        gridPropertiesFile.withWriterAppend('UTF-8') { fileWriter ->
            fileWriter.writeLine ''
            props.each { key, value ->
                fileWriter.writeLine "$key=$value"
            }
        }

        props.load(gridPropertiesFile.newDataInputStream())
        println("New GRID URL=" + props.getProperty('grid.hub.url'))

    }

} catch (Exception ex) {
    println ex.message
}

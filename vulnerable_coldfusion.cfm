
<cfquery name="GetUser" datasource="your_datasource">
    SELECT * FROM users
    WHERE username = '#URL.param#'
</cfquery>

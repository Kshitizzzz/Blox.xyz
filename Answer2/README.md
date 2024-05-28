# 2. There is one database. Let’s say it is hosted locally and one of the team members migrates it to AWS or GCP. How can one confirm that the copied data is the same as the original data ? What would be the check points ? Imagine that data from table is of the form : List<Map<String,String>>


1. Pre-Migration Checkpoints
    - Schema Verification: Ensure that the database schema (tables, indexes, constraints) on the cloud database matches the local database schema.
    - Row Counts: Compare the number of rows in each table in both databases to ensure no rows are missing or added during migration.

2. Data Integrity Checks
    - Checksum or Hash Verification: Calculate hashes of the data from each table. This can be done by creating a hash for each row.

3. Random Sampling:
    - Sample Row Comparison:
        - Randomly select a set of rows from each table and compare the data field-by-field between the local and cloud databases.
        - Ensure that selected rows from both databases match exactly.


4. Post-Migration Validation
    - Query Results Comparison:
        - Run a series of predefined queries against both the local and cloud databases and compare the results.
        - Ensure that the results are identical, indicating consistent data.

    - Application Testing:
        - Perform application-level tests to ensure that the application interacts with the cloud database as expected and returns correct data.
     
5. AWS (Amazon Web Services)
    - AWS DMS (Database Migration Service) can be used for data migration and supports validation tasks. It includes data validation capabilities that compare the source and target data.

    - DMS Validation Report provides detailed insights into the consistency of the data after migration.

### Example Checkpoints for List<Map<String,String>> Data: 

1. Schema and Structure Verification:
    - Verify that the structure of the data in the form of List<Map<String, String>> remains unchanged.

2. Row Count Check:
    - Ensure that the size of the list (number of rows) in both local and cloud databases is the same.

3. Checksum/Hash Verification:
    - Calculate a hash for each map in the list and compare corresponding hashes between local and cloud databases.
  
4. Data Sampling
    - Randomly select a sample of records and compare them between the original and copied databases.
  
5. Data validation script
    - Write a validation script which compares data row by row of both databases.

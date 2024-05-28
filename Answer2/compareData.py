import hashlib

# func to create hash 
def compute_hash(row):
    hash_obj = hashlib.sha256()
    for key, value in sorted(row.items()):
        hash_obj.update(f"{key}:{value}".encode('utf-8'))
    return hash_obj.hexdigest()

# function use to compare hash of each row of source and target data
def compare_data(source_data, target_data):
    source_hashes = {compute_hash(row): row for row in source_data}
    target_hashes = {compute_hash(row): row for row in target_data}
    
    source_set = set(source_hashes.keys())
    target_set = set(target_hashes.keys())
    
    # Check if hashes are not matched then finding the mismatched row
    if source_set != target_set:
        print("Data mismatch found!")
        missing_in_target = source_set - target_set
        missing_in_source = target_set - source_set
        if missing_in_target:
            print("Rows missing in target data:")
            for h in missing_in_target:
                print(source_hashes[h])
        if missing_in_source:
            print("Rows missing in source data:")
            for h in missing_in_source:
                print(target_hashes[h])
        return False
    else:
        print("Data successfully matched.")
        return True

# Example data (List of Maps)
# In actual scenario connect with source that is local database and target that is AWS Database
# and fetch data from both the database and pass that data to a function.
    
# Since the size of the data may be large so in that scenario try to send data to the function in batches 
# and keep record of last row fetched.
    
# In this code I prefer to compare hash of each rows instead of comparing hash of the complete data because
# 1. Easy to identify missing/defaulted row.
# 2. Easy to debug.
# 3. Less Complex
source_data = [
    {"id": "1", "name": "Alice", "email": "alice@example.com","xtz":"gds"},
    {"id": "2", "name": "Bob", "email": "bob@example.com"},
]
target_data = [
    {"id": "1", "name": "Alice", "email": "alice@example.com","xtz":"gds"},
    {"id": "2", "name": "Bob", "email": "bob@example.com"},
]

# Compare data
compare_data(source_data, target_data)

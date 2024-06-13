import matplotlib.pyplot as plt
import re
import os

# Directory containing the files
directory_path = './'

# Function to read balances from a file
def read_balances(file_path):
    balances = []
    with open(file_path, 'r') as file:
        for line in file:
            match = re.search(r'Current balance: (\d+\.\d+)', line)
            if match:
                balances.append(float(match.group(1)))
    return balances

# Get all files with 'bidder' in their name
files = [f for f in os.listdir(directory_path) if 'bidder' in f]

# Initialize a dictionary to store balances for each file
all_balances = {}

# Read balances from each file
for file_name in files:
    file_path = os.path.join(directory_path, file_name)
    balances = read_balances(file_path)
    all_balances[file_name] = balances

# Plot balances for each file
plt.figure(figsize=(10, 6))
for file_name, balances in all_balances.items():
    plt.plot(balances, label=file_name)

plt.title('Bidder Balances Over Time')
plt.xlabel('Time (arbitrary units)')
plt.ylabel('Balance')
plt.legend()
plt.grid(True)
plt.show()

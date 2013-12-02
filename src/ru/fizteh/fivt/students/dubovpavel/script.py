import os

def lines(file):
	return len([line for line in open(file).read().split('\n') if len(line.strip()) > 1])
	
res = 0	
for root, dirs, files in os.walk('.'):
		for file in files:
			if file != 'script.py':
				res += lines(os.path.join(root, file))

print(res)
	
import csv
import numpy as np
from sklearn.cluster import KMeans
from sklearn import tree
from sklearn.metrics import accuracy_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import make_classification
from sklearn import svm
import matplotlib
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE
from sklearn.model_selection import KFold
from scipy.stats import mode
from sklearn.utils import shuffle
from sklearn.metrics import precision_score, recall_score
from sklearn.feature_selection import SelectFromModel

arr = []
arr_complexities = []
arr_names = []
complexity_dictionary = {'n':0, 'n_square':1, 'logn':2, 'nlogn':3, '1':4}
color_mapping = {0:'r', 1:'g', 2:'b', 3:'y', 4:'m'}
file_to_complexity_mapping = {}

with open('./finalFeatureData.csv','rt') as f:
	data = csv.reader(f)
	count = 0
	for row in data:
		count = count + 1
		if(count==1):
			continue
		name = row[-1]
		features = row[13]
		complexity = row[-2]
		if(complexity=='n'  or complexity=='1' or complexity=='logn'):
			continue
		for i in features:
			i = (int)(i)
		arr_names.append(name)
		arr.append(features)
		arr_complexities.append(complexity_dictionary[complexity])

arr = np.asarray(arr).reshape(-1, 1)
arr_complexities = np.asarray(arr_complexities)

# shuffle the data
arr, arr_complexities, arr_names = shuffle(arr, arr_complexities, arr_names, random_state=0)
print(arr.shape)
no_of_variables = 14
scores = []
precisions = []
recalls = []

X_train, X_test = arr[:(int)(0.9*len(arr))], arr[(int)(0.9*len(arr)):]
y_train, y_test = arr_complexities[:(int)(0.9*len(arr))], arr_complexities[(int)(0.9*len(arr)):]
train_names = arr_names[:(int)(0.9*len(arr))]
test_names = arr_names[(int)(0.8*len(arr)):]

classifier = RandomForestClassifier(n_estimators=100, max_depth=4, random_state=0)
classifier.fit(X_train, y_train)
y_predicted = classifier.predict(X_test)
acc_score = accuracy_score(y_test, y_predicted)

print(y_test)
print(y_predicted)
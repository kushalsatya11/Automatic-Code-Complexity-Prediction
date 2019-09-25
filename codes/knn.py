import csv
import numpy as np
from sklearn.cluster import KMeans
from sklearn import tree
from sklearn.metrics import accuracy_score
from sklearn.neighbors import KNeighborsClassifier
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
		features = row[0:14]
		complexity = row[-2]
		if(complexity==('logn' or 'nlogn')):
			continue
		for i in features:
			i = (int)(i)
		arr_names.append(name)
		arr.append(features)
		arr_complexities.append(complexity_dictionary[complexity])

arr = np.asarray(arr, dtype=float)
arr_complexities = np.asarray(arr_complexities)

# shuffle the data
arr, arr_complexities, arr_names = shuffle(arr, arr_complexities, arr_names, random_state=0)

no_of_variables = 14
scores = []
precisions = []
recalls = []

for i in range(1, no_of_variables):
	score = []
	prec = []
	rec = []
	array = arr[:, :i]
	kf = KFold(n_splits=5, shuffle=True)
	for train_index, test_index in kf.split(array, arr_complexities):
		X_train, X_test = array[train_index], array[test_index]
		y_train, y_test = arr_complexities[train_index], arr_complexities[test_index]

		neigh = KNeighborsClassifier(n_neighbors=4)
		neigh.fit(X_train, y_train)
		y_predicted = neigh.predict(X_test)
		acc_score = accuracy_score(y_test, y_predicted)
		score.append(acc_score)
		prec.append(precision_score(y_test, y_predicted, average='weighted'))
		rec.append(recall_score(y_test, y_predicted, average='weighted'))
	scores.append(max(score))
	precisions.append(max(prec))
	recalls.append(max(rec))


print('Scores: ', scores)
plt.plot(scores)
plt.savefig('./knn_vs_variable_count.png')

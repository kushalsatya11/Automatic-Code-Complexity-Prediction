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
from sklearn.decomposition import PCA
from sklearn.metrics import precision_recall_curve
from sklearn.feature_selection import SelectKBest, chi2
import scikitplot as skplt
from sklearn.metrics import precision_score, recall_score

arr = []
arr_complexities = []
arr_names = []
complexity_dictionary = {'n':0, 'n_square':1, '1':2, 'nlogn':3, 'logn':4}
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
		features = row[10]
		complexity = row[-2]
		if(complexity=='n' or complexity=='n_square'):
			continue
		for i in features:
			i = (int)(i)
		arr_names.append(name)
		arr.append(features)
		arr_complexities.append(complexity_dictionary[complexity])

arr = np.asarray(arr, dtype=float).reshape(-1, 1)
arr_complexities = np.asarray(arr_complexities)

# shuffle the data
arr, arr_complexities, arr_names = shuffle(arr, arr_complexities, arr_names, random_state=0)

accuracy_arr = []
recall_arr = []
precision_arr = []

n_classes = 5
no_of_variables = 14
scores = []
precisions= []
recalls = []


for i in range(1, no_of_variables):
	array = arr[:, :]
	# pca = PCA(n_components=i)
	# pca.fit(arr)
	# array = pca.transform(arr)
	kf = KFold(n_splits=5, shuffle=False) 
	count = 0
	score = []
	prec = []
	rec = []
	
	for train_index, test_index in kf.split(array, arr_complexities):
		count += 1
		X_train, X_test = array[train_index], array[test_index]
		y_train, y_test = arr_complexities[train_index], arr_complexities[test_index]
		kmeans = KMeans(n_clusters=n_classes, random_state=0).fit(X_train)
		y_predicted = kmeans.predict(X_test)
		labels = np.zeros_like(y_predicted)

		labels_train = np.zeros_like(y_train)
		for i in range(n_classes):
			mask_train = (y_train == i)
			labels_train[mask_train] = mode(y_train[mask_train])[0]

		for i in range(n_classes):
			mask = (y_predicted == i)
			labels[mask] = mode(y_test[mask])[0]
		acc_score = accuracy_score(y_test, labels)
		prec_ = precision_score(y_test, labels, average='weighted')
		rec_ = recall_score(y_test, labels, average='weighted')
		score.append(acc_score)
		prec.append(prec_)
		rec.append(rec_)
	scores.append(max(score))
	precisions.append(max(prec))
	recalls.append(max(rec))

print('Scores: ', scores)

plt.plot(scores, "s-")
plt.savefig('./kmeans_vs_variable_count.png')
